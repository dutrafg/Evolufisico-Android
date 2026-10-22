package br.com.evolufisico.nativo.data

import org.json.JSONObject
import java.time.LocalDate
import java.util.UUID

fun JSONObject.optionalDouble(key: String): Double? =
    if (isNull(key) || !has(key)) null else optDouble(key).takeIf { it.isFinite() }
fun JSONObject.optionalInt(key: String): Int? =
    if (isNull(key) || !has(key)) null else optInt(key)
fun JSONObject.numberMap(key: String): Map<String, Double> {
    val source = optJSONObject(key) ?: return emptyMap()
    return source.keys().asSequence().mapNotNull { k -> source.optionalDouble(k)?.let { k to it } }.toMap()
}
fun JSONObject.textMap(key: String): Map<String, String> {
    val source = optJSONObject(key) ?: return emptyMap()
    return source.keys().asSequence().map { it to source.optString(it, "") }.toMap()
}
private fun <T> JSONObject.putMap(key: String, values: Map<String, T>) = put(key, JSONObject().apply {
    for ((field, value) in values) put(field, value)
})

/** The permanent studentId links re-evaluations, appointments and intake forms. */
data class Evaluation(
    val id: String = UUID.randomUUID().toString(),
    val studentId: String = UUID.randomUUID().toString(),
    val name: String = "", val sex: String = "Não informado", val age: Int? = null,
    val date: String = LocalDate.now().toString(), val weightKg: Double? = null,
    val heightCm: Double? = null, val waistCm: Double? = null, val hipCm: Double? = null,
    val bodyFatPercent: Double? = null, val trainingExperienced: Boolean = false,
    val dietExperienced: Boolean = false, val trainingAdherence: Int? = null,
    val dietAdherence: Int? = null, val notes: String = "",
    val email: String = "", val phone: String = "", val nextAssessment: String = "",
    val phase: String = "", val week: String = "", val energy: Int? = null,
    val professional: String = "", val registration: String = "", val clinic: String = "",
    val perimeters: Map<String, Double> = emptyMap(),
    val skinfolds: Map<String, Double> = emptyMap(),
    val bioimpedance: Map<String, Double> = emptyMap(),
    val photos: Map<String, String> = emptyMap(),
    val extendedAnswers: Map<String, String> = emptyMap(),
    val goalWeightKg: Double? = null, val goalWeightBaselineKg: Double? = null,
    val goalBodyFatPercent: Double? = null, val goalBodyFatBaselinePercent: Double? = null,
    val goalDate: String = "", val goalNotes: String = ""
) {
    fun newCheckIn() = Evaluation(studentId = studentId, name = name, sex = sex, age = age,
        heightCm = heightCm, email = email, phone = phone, professional = professional,
        registration = registration, clinic = clinic, nextAssessment = "",
        goalWeightKg = goalWeightKg, goalWeightBaselineKg = goalWeightBaselineKg,
        goalBodyFatPercent = goalBodyFatPercent, goalBodyFatBaselinePercent = goalBodyFatBaselinePercent,
        goalDate = goalDate, goalNotes = goalNotes)
    fun toJson() = JSONObject().apply {
        put("id", id); put("studentId", studentId); put("name", name); put("sex", sex)
        put("age", age ?: JSONObject.NULL); put("date", date)
        put("weightKg", weightKg ?: JSONObject.NULL); put("heightCm", heightCm ?: JSONObject.NULL)
        put("waistCm", waistCm ?: JSONObject.NULL); put("hipCm", hipCm ?: JSONObject.NULL)
        put("bodyFatPercent", bodyFatPercent ?: JSONObject.NULL)
        put("trainingExperienced", trainingExperienced); put("dietExperienced", dietExperienced)
        put("trainingAdherence", trainingAdherence ?: JSONObject.NULL)
        put("dietAdherence", dietAdherence ?: JSONObject.NULL); put("notes", notes)
        put("email", email); put("phone", phone); put("nextAssessment", nextAssessment)
        put("phase", phase); put("week", week); put("energy", energy ?: JSONObject.NULL)
        put("professional", professional); put("registration", registration); put("clinic", clinic)
        putMap("perimeters", perimeters); putMap("skinfolds", skinfolds)
        putMap("bioimpedance", bioimpedance); putMap("photos", photos)
        putMap("extendedAnswers", extendedAnswers)
        put("goalWeightKg", goalWeightKg ?: JSONObject.NULL); put("goalWeightBaselineKg", goalWeightBaselineKg ?: JSONObject.NULL)
        put("goalBodyFatPercent", goalBodyFatPercent ?: JSONObject.NULL)
        put("goalBodyFatBaselinePercent", goalBodyFatBaselinePercent ?: JSONObject.NULL)
        put("goalDate", goalDate); put("goalNotes", goalNotes)
    }
    companion object {
        fun fromJson(o: JSONObject) = Evaluation(
            id = o.getString("id"), studentId = o.getString("studentId"),
            name = o.getString("name"), sex = o.optString("sex", "Não informado"),
            age = o.optionalInt("age"), date = o.getString("date"),
            weightKg = o.optionalDouble("weightKg"), heightCm = o.optionalDouble("heightCm"),
            waistCm = o.optionalDouble("waistCm"), hipCm = o.optionalDouble("hipCm"),
            bodyFatPercent = o.optionalDouble("bodyFatPercent"),
            trainingExperienced = o.optBoolean("trainingExperienced"),
            dietExperienced = o.optBoolean("dietExperienced"),
            trainingAdherence = o.optionalInt("trainingAdherence"),
            dietAdherence = o.optionalInt("dietAdherence"), notes = o.optString("notes", ""),
            email = o.optString("email", ""), phone = o.optString("phone", ""),
            nextAssessment = o.optString("nextAssessment", ""),
            phase = o.optString("phase", ""), week = o.optString("week", ""),
            energy = o.optionalInt("energy"), professional = o.optString("professional", ""),
            registration = o.optString("registration", ""), clinic = o.optString("clinic", ""),
            perimeters = o.numberMap("perimeters"), skinfolds = o.numberMap("skinfolds"),
            bioimpedance = o.numberMap("bioimpedance"), photos = o.textMap("photos"),
            extendedAnswers = o.textMap("extendedAnswers"),
            goalWeightKg = o.optionalDouble("goalWeightKg"), goalWeightBaselineKg = o.optionalDouble("goalWeightBaselineKg"),
            goalBodyFatPercent = o.optionalDouble("goalBodyFatPercent"),
            goalBodyFatBaselinePercent = o.optionalDouble("goalBodyFatBaselinePercent"),
            goalDate = o.optString("goalDate", ""), goalNotes = o.optString("goalNotes", "")
        )
    }
}

data class Charge(
    val id: String = UUID.randomUUID().toString(), val student: String = "",
    val service: String = "Avaliação física", val amountCents: Long = 0,
    val dueDate: String = LocalDate.now().toString(), val paid: Boolean = false,
    val secondStudent: String = "", val paymentMethod: String = "Pix",
    val installments: Int = 1, val discountCents: Long = 0,
    val paidDate: String = "", val notes: String = "",
    val studentId: String = "", val secondStudentId: String = "", val exempt: Boolean = false
) {
    fun toJson() = JSONObject().apply {
        put("id", id); put("student", student); put("service", service)
        put("amountCents", amountCents); put("dueDate", dueDate); put("paid", paid)
        put("secondStudent", secondStudent); put("paymentMethod", paymentMethod)
        put("installments", installments); put("discountCents", discountCents)
        put("paidDate", paidDate); put("notes", notes)
        put("studentId", studentId); put("secondStudentId", secondStudentId); put("exempt", exempt)
    }
    companion object {
        fun fromJson(o: JSONObject) = Charge(id = o.getString("id"),
            student = o.getString("student"), service = o.optString("service", "Avaliação física"),
            amountCents = o.getLong("amountCents"), dueDate = o.getString("dueDate"),
            paid = o.optBoolean("paid"), secondStudent = o.optString("secondStudent", ""),
            paymentMethod = o.optString("paymentMethod", "Pix"),
            installments = o.optInt("installments", 1), discountCents = o.optLong("discountCents"),
            paidDate = o.optString("paidDate", ""), notes = o.optString("notes", ""),
            studentId = o.optString("studentId", ""), secondStudentId = o.optString("secondStudentId", ""),
            exempt = o.optBoolean("exempt"))
    }
}

data class Intake(
    val id: String = UUID.randomUUID().toString(), val student: String = "",
    val level: String = "Iniciante", val date: String = LocalDate.now().toString(),
    val objective: String = "", val healthHistory: String = "", val routine: String = "",
    val observations: String = "", val studentId: String = "",
    val answers: Map<String,String> = emptyMap()
) {
    fun toJson() = JSONObject().apply {
        put("id", id); put("student", student); put("level", level); put("date", date)
        put("objective", objective); put("healthHistory", healthHistory)
        put("routine", routine); put("observations", observations)
        put("studentId", studentId); putMap("answers", answers)
    }
    companion object {
        fun fromJson(o: JSONObject) = Intake(id = o.getString("id"),
            student = o.getString("student"), level = o.optString("level", "Iniciante"),
            date = o.getString("date"), objective = o.optString("objective"),
            healthHistory = o.optString("healthHistory"), routine = o.optString("routine"),
            observations = o.optString("observations"), studentId = o.optString("studentId", ""),
            answers = o.textMap("answers"))
    }
}

data class Appointment(
    val id: String = UUID.randomUUID().toString(), val student: String = "",
    val date: String = LocalDate.now().toString(), val time: String = "09:00",
    val durationMinutes: Int = 60, val type: String = "Avaliação",
    val location: String = "", val notes: String = "", val status: String = "Agendado",
    val studentId: String = ""
) {
    fun toJson() = JSONObject().apply {
        put("id", id); put("student", student); put("date", date); put("time", time)
        put("durationMinutes", durationMinutes); put("type", type); put("location", location)
        put("notes", notes); put("status", status); put("studentId", studentId)
    }
    companion object {
        fun fromJson(o: JSONObject) = Appointment(id = o.getString("id"), student = o.optString("student"),
            date = o.getString("date"), time = o.optString("time", "09:00"),
            durationMinutes = o.optInt("durationMinutes", 60), type = o.optString("type", "Avaliação"),
            location = o.optString("location"), notes = o.optString("notes"),
            status = o.optString("status", "Agendado"), studentId = o.optString("studentId"))
    }
}

data class TaskItem(
    val id: String = UUID.randomUUID().toString(), val title: String = "",
    val dueDate: String = LocalDate.now().toString(), val done: Boolean = false,
    val notes: String = "", val priority:Int=1, val category:String="Geral",
    val studentId:String="",val student:String=""
) {
    fun toJson() = JSONObject().put("id", id).put("title", title)
        .put("dueDate", dueDate).put("done", done).put("notes", notes)
        .put("priority",priority).put("category",category).put("studentId",studentId).put("student",student)
    companion object {
        fun fromJson(o: JSONObject) = TaskItem(o.getString("id"), o.getString("title"),
            o.getString("dueDate"), o.optBoolean("done"), o.optString("notes"),
            o.optInt("priority",1),o.optString("category","Geral"),o.optString("studentId"),o.optString("student"))
    }
}
