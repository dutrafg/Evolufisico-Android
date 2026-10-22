package br.com.evolufisico.nativo.system

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.*
import br.com.evolufisico.nativo.data.Appointment
import br.com.evolufisico.nativo.data.CryptoStore
import br.com.evolufisico.nativo.domain.ReminderPolicy
import br.com.evolufisico.nativo.MainActivity
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

/** Approximate local reminders, controlled by the user. Not an exact alarm. */
object AppointmentNotifications {
    const val CHANNEL_ID="evolufisico-agenda"
    private fun enabled(context:Context)=context.getSharedPreferences("native-notifications",Context.MODE_PRIVATE)
        .getBoolean("enabled",false)
    fun leadMinutes(context:Context)=runCatching {
        ReminderPolicy.validateLead(context.getSharedPreferences("native-notifications",Context.MODE_PRIVATE)
            .getInt("lead-minutes",60))
    }.getOrDefault(60)
    fun permitted(context:Context):Boolean=Build.VERSION.SDK_INT<33 ||
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)==PackageManager.PERMISSION_GRANTED
    fun configure(context:Context,enabled:Boolean){
        context.getSharedPreferences("native-notifications",Context.MODE_PRIVATE).edit().putBoolean("enabled",enabled).apply()
    }
    fun channel(context:Context){
        val service=context.getSystemService(NotificationManager::class.java)
        service.createNotificationChannel(NotificationChannel(CHANNEL_ID,"Agenda e reavaliações",NotificationManager.IMPORTANCE_DEFAULT))
    }
    fun schedule(context:Context,item:Appointment){
        val unique="ef-appointment-${item.id}"
        if(!enabled(context)||!permitted(context)||item.status in listOf("Cancelado","Concluído","Faltou")){
            WorkManager.getInstance(context).cancelUniqueWork(unique);return
        }
        val date=runCatching{LocalDateTime.of(java.time.LocalDate.parse(item.date),LocalTime.parse(item.time))}.getOrNull()
        if(date==null){cancel(context,item.id);return}
        val lead=leadMinutes(context)
        val whenNotify=date.minusMinutes(lead.toLong())
        val delay=Duration.between(LocalDateTime.now(),whenNotify).toMillis()
        if(!ReminderPolicy.shouldSchedule(Duration.between(LocalDateTime.now(),date).toMillis(),lead)){WorkManager.getInstance(context).cancelUniqueWork(unique);return}
        // Keep personal details out of the WorkManager database.
        val data=workDataOf("id" to item.id,"lead-minutes" to lead)
        WorkManager.getInstance(context).enqueueUniqueWork(unique,ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<AppointmentNotificationWorker>()
                .setInputData(data).setInitialDelay(delay,TimeUnit.MILLISECONDS).build())
    }
    fun cancel(context:Context,id:String)=WorkManager.getInstance(context).cancelUniqueWork("ef-appointment-$id")
}
class AppointmentNotificationWorker(context:Context,params:WorkerParameters):Worker(context,params){
    override fun doWork():Result { return try{
        if(!AppointmentNotifications.permitted(applicationContext))return Result.success()
        if(!applicationContext.getSharedPreferences("native-notifications",Context.MODE_PRIVATE).getBoolean("enabled",false))return Result.success()
        val id=inputData.getString("id") ?: return Result.success()
        val item=CryptoStore(applicationContext).use { store ->
            store.list("appointment").firstOrNull { it.optString("id")==id }?.let(Appointment::fromJson)
        } ?: return Result.success()
        if(item.status !in listOf("Agendado","Confirmado"))return Result.success()
        val scheduled=runCatching { LocalDateTime.of(java.time.LocalDate.parse(item.date),LocalTime.parse(item.time)) }
            .getOrNull() ?: return Result.success()
        val now=LocalDateTime.now()
        val lead=ReminderPolicy.validateLead(inputData.getInt("lead-minutes",60))
        val secondsUntil=Duration.between(now,scheduled).seconds
        if(!ReminderPolicy.shouldNotify(secondsUntil,lead))return Result.success()
        AppointmentNotifications.channel(applicationContext)
        val who=item.student.take(80)
        val appointment=item.type.take(80)
        val time="${item.date} ${item.time}"
        val open=PendingIntent.getActivity(applicationContext,id.hashCode(),
            Intent(applicationContext,MainActivity::class.java),PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val publicNotice=NotificationCompat.Builder(applicationContext,AppointmentNotifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar).setContentTitle("EvoluFísico")
            .setContentText("Você tem um atendimento próximo.").build()
        val notice=NotificationCompat.Builder(applicationContext,AppointmentNotifications.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
            .setContentTitle("EvoluFísico • $appointment")
            .setContentText("$who às $time")
            .setContentIntent(open)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setPublicVersion(publicNotice)
            .setAutoCancel(true).build()
        val manager=applicationContext.getSystemService(NotificationManager::class.java)
        manager.notify(id.hashCode(),notice)
        Result.success()
    }catch(_:Exception){if(runAttemptCount<3)Result.retry() else Result.failure()}
    }
}
