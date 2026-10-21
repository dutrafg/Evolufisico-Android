package br.com.evolufisico.android.agenda

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import br.com.evolufisico.android.MainActivity
import br.com.evolufisico.android.ops.OperationalStore
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/** Frequência aproximada; restrições de bateria podem atrasar avisos. */
class AgendaReminderScheduler(private val context:Context) {
    private val settings=context.getSharedPreferences("agenda_reminders",Context.MODE_PRIVATE)
    fun isEnabled():Boolean=settings.getBoolean("enabled",false)
    fun hasPermission():Boolean =
        (Build.VERSION.SDK_INT<33 ||
            ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)==
                PackageManager.PERMISSION_GRANTED) &&
        NotificationManagerCompat.from(context).areNotificationsEnabled()

    fun setEnabled(enabled:Boolean) {
        if(enabled) {
            require(hasPermission()) { "Autorize as notificações nas configurações do Android." }
            require(settings.edit().putBoolean("enabled",true).commit()) {
                "Não foi possível salvar as preferências de lembretes."
            }
            try {
                val request=PeriodicWorkRequestBuilder<AgendaReminderWorker>(15,TimeUnit.MINUTES)
                    .build()
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "evolufisico-agenda-reminders",ExistingPeriodicWorkPolicy.KEEP,request)
            } catch(e:Exception) {
                settings.edit().putBoolean("enabled",false).commit()
                throw e
            }
        } else {
            check(settings.edit().putBoolean("enabled",false).remove("notified").commit())
            WorkManager.getInstance(context).cancelUniqueWork("evolufisico-agenda-reminders")
        }
    }
}

class AgendaReminderWorker(ctx:Context, params:WorkerParameters):CoroutineWorker(ctx,params) {
    override suspend fun doWork():Result {
        val app=applicationContext
        val scheduler=AgendaReminderScheduler(app)
        if(!scheduler.isEnabled() || !scheduler.hasPermission()) return Result.success()
        return runCatching {
            val appointments=OperationalStore(app).load().appointments
            val prefs=app.getSharedPreferences("agenda_reminders",Context.MODE_PRIVATE)
            val existing=prefs.getStringSet("notified",emptySet()).orEmpty().toSet()
            val upcoming=AgendaReminderRules.upcoming(appointments,LocalDateTime.now(),existing)
            if(upcoming.isNotEmpty()) {
                val manager=app.getSystemService(NotificationManager::class.java)
                manager.createNotificationChannel(NotificationChannel(
                    "evolufisico_agenda","Avisos de atendimentos",
                    NotificationManager.IMPORTANCE_DEFAULT))
                val launch=PendingIntent.getActivity(app,0,
                    Intent(app,MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                val message=if(upcoming.size==1)
                    "Há 1 atendimento previsto para a próxima hora."
                    else "Há ${upcoming.size} atendimentos previstos para a próxima hora."
                val note=NotificationCompat.Builder(app,"evolufisico_agenda")
                    .setSmallIcon(android.R.drawable.ic_popup_reminder)
                    .setContentTitle("Agenda: atendimento próximo")
                    .setContentText(message)
                    .setContentIntent(launch)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build()
                NotificationManagerCompat.from(app).notify(7117,note)
                // Inclui somente registros ainda existentes; restringe crescimento.
                val valid=appointments.asSequence().filter { a ->
                    runCatching {
                        !LocalDateTime.parse(a.startIso).isBefore(LocalDateTime.now().minusDays(1))
                    }.getOrDefault(false)
                }.map(AgendaReminderRules::key).toSet()
                // Os avisos recém-enviados têm prioridade mesmo se o histórico
                // antigo contiver mais de mil registros.
                val keys=(upcoming.map(AgendaReminderRules::key)+
                    (existing intersect valid)).take(1000).toSet()
                check(prefs.edit().putStringSet("notified",keys).commit()) {
                    "Não foi possível registrar o lembrete enviado."
                }
            }
            Result.success()
        }.getOrElse { if(runAttemptCount<2) Result.retry() else Result.failure() }
    }
}
