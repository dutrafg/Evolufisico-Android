package br.com.evolufisico.nativo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.Locale
import org.json.JSONArray
import org.json.JSONObject

data class Student(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val birthDate: String
)

data class Appointment(
    val id: Long,
    val title: String,
    val date: String,
    val time: String,
    val location: String,
    val notes: String,
    val kind: String = "Atendimento"
)

data class TaskRecord(val id: Long, val title: String, val dueDate: String, val completed: Boolean)
data class IntakeForm(val id: Long, val studentId: Long?, val template: String, val date: String, val responses: String)
data class ChargeRecord(val id: Long, val studentId: Long?, val title: String, val amount: Double, val dueDate: String, val status: String, val notes: String)

data class Assessment(
    val id: Long,
    val studentId: Long,
    val date: String,
    val weightKg: Double,
    val heightCm: Double,
    val waistCm: Double?,
    val hipCm: Double?,
    val notes: String,
    val bodyFatPercent: Double? = null,
    val neckCm: Double? = null,
    val chestCm: Double? = null,
    val armCm: Double? = null,
    val thighCm: Double? = null,
    val sex: String = "Masculino",
    val ageYears: Int? = null,
    val tricepsFoldMm: Double? = null,
    val subscapularFoldMm: Double? = null,
    val chestFoldMm: Double? = null,
    val axillaryFoldMm: Double? = null,
    val suprailiacFoldMm: Double? = null,
    val abdominalFoldMm: Double? = null,
    val thighFoldMm: Double? = null,
    val bioimpedanceBodyFatPercent: Double? = null,
    val bioimpedanceLeanMassKg: Double? = null,
    val bioimpedanceDevice: String = "",
    val photoData: List<String> = emptyList(),
    val contextEvent: String = ""
) {
    val bmi: Double? get() = AssessmentMath.bmi(weightKg, heightCm)
    val pollock7: BodyFatEstimate? get() = ageYears?.let {
        AssessmentMath.pollock7(
            sex, it, weightKg,
            listOf(tricepsFoldMm, subscapularFoldMm, chestFoldMm, axillaryFoldMm, suprailiacFoldMm, abdominalFoldMm, thighFoldMm)
        )
    }
    val pollock3: BodyFatEstimate? get() = ageYears?.let {
        AssessmentMath.pollock3(
            sex, it, weightKg, chestFoldMm, abdominalFoldMm,
            tricepsFoldMm, suprailiacFoldMm, thighFoldMm
        )
    }
}

class LocalDatabase(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "evolufisico-native.db", null, 9) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE students (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                phone TEXT NOT NULL DEFAULT '',
                email TEXT NOT NULL DEFAULT '',
                birth_date TEXT NOT NULL DEFAULT '',
                created_at TEXT NOT NULL
            )"""
        )
        db.execSQL(
            """CREATE TABLE assessments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
                date TEXT NOT NULL,
                weight_kg REAL NOT NULL,
                height_cm REAL NOT NULL,
                waist_cm REAL,
                hip_cm REAL,
                body_fat_percent REAL,
                neck_cm REAL,
                chest_cm REAL,
                arm_cm REAL,
                thigh_cm REAL,
                notes TEXT NOT NULL DEFAULT '',
                sex TEXT NOT NULL DEFAULT 'Masculino',
                age_years INTEGER,
                fold_triceps_mm REAL,
                fold_subscapular_mm REAL,
                fold_chest_mm REAL,
                fold_axillary_mm REAL,
                fold_suprailiac_mm REAL,
                fold_abdominal_mm REAL,
                fold_thigh_mm REAL,
                bia_body_fat_percent REAL,
                bia_lean_mass_kg REAL,
                bia_device TEXT NOT NULL DEFAULT '',
                photo_data_json TEXT NOT NULL DEFAULT '[]',
                context_event TEXT NOT NULL DEFAULT ''
            )"""
        )
        db.execSQL("CREATE INDEX index_assessments_student_date ON assessments(student_id, date DESC, id DESC)")
        db.execSQL("CREATE INDEX index_students_name ON students(name COLLATE NOCASE)")
        createAppointments(db)
        createBeta3Tables(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) createAppointments(db)
        if (oldVersion < 3) createBeta3Tables(db)
        if (oldVersion < 4) {
            listOf("body_fat_percent REAL", "neck_cm REAL", "chest_cm REAL", "arm_cm REAL", "thigh_cm REAL")
                .forEach { column -> db.execSQL("ALTER TABLE assessments ADD COLUMN $column") }
        }
        if (oldVersion < 5 && !hasColumn(db, "appointments", "kind")) {
            db.execSQL("ALTER TABLE appointments ADD COLUMN kind TEXT NOT NULL DEFAULT 'Atendimento'")
            db.execSQL("""UPDATE appointments
                SET kind = CASE
                    WHEN lower(title) LIKE '%retorno%' OR lower(title) LIKE '%reavalia%' THEN 'Retorno'
                    WHEN lower(title) LIKE '%avalia%' THEN 'Avaliação'
                    ELSE 'Atendimento'
                END""")
        }
        if (oldVersion < 6) {
            listOf(
                "sex TEXT NOT NULL DEFAULT 'Masculino'", "age_years INTEGER", "fold_triceps_mm REAL",
                "fold_subscapular_mm REAL", "fold_chest_mm REAL", "fold_axillary_mm REAL",
                "fold_suprailiac_mm REAL", "fold_abdominal_mm REAL", "fold_thigh_mm REAL"
            ).forEach { column -> db.execSQL("ALTER TABLE assessments ADD COLUMN $column") }
        }
        if (oldVersion < 7) {
            listOf("bia_body_fat_percent REAL", "bia_lean_mass_kg REAL", "bia_device TEXT NOT NULL DEFAULT ''")
                .forEach { column -> db.execSQL("ALTER TABLE assessments ADD COLUMN $column") }
        }
        if (oldVersion < 8) db.execSQL("ALTER TABLE assessments ADD COLUMN photo_data_json TEXT NOT NULL DEFAULT '[]'")
        if (oldVersion < 9) db.execSQL("ALTER TABLE assessments ADD COLUMN context_event TEXT NOT NULL DEFAULT ''")
    }

    private fun createBeta3Tables(db: SQLiteDatabase) {
        db.execSQL("""CREATE TABLE IF NOT EXISTS tasks (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            due_date TEXT NOT NULL,
            completed INTEGER NOT NULL DEFAULT 0
        )""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS intake_forms (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id INTEGER REFERENCES students(id) ON DELETE SET NULL,
            template TEXT NOT NULL,
            date TEXT NOT NULL,
            responses TEXT NOT NULL DEFAULT ''
        )""")
        db.execSQL("""CREATE TABLE IF NOT EXISTS charges (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            student_id INTEGER REFERENCES students(id) ON DELETE SET NULL,
            title TEXT NOT NULL,
            amount REAL NOT NULL,
            due_date TEXT NOT NULL,
            status TEXT NOT NULL DEFAULT 'Pendente',
            notes TEXT NOT NULL DEFAULT ''
        )""")
        db.execSQL("CREATE INDEX IF NOT EXISTS index_charges_due_status ON charges(due_date, status)")
    }

    private fun createAppointments(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                location TEXT NOT NULL DEFAULT '',
                notes TEXT NOT NULL DEFAULT '',
                kind TEXT NOT NULL DEFAULT 'Atendimento'
            )"""
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_appointments_date_time ON appointments(date, time)")
    }

    @Synchronized
    fun appointments(date: String? = null): List<Appointment> {
        val result = mutableListOf<Appointment>()
        readableDatabase.query(
            "appointments",
            arrayOf("id", "title", "date", "time", "location", "notes", "kind"),
            if (date == null) null else "date=?",
            if (date == null) null else arrayOf(date),
            null, null, "date ASC, time ASC, id ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += Appointment(
                    cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)
                )
            }
        }
        return result
    }

    @Synchronized
    fun saveAppointment(id: Long?, title: String, date: String, time: String, location: String, notes: String, kind: String = "Atendimento"): Long {
        require(title.isNotBlank()) { "Informe o motivo do atendimento." }
        require(validDate(date)) { "Confira a data do atendimento." }
        require(time.matches(Regex("\\d{2}:\\d{2}")) && time.substring(0, 2).toInt() in 0..23 && time.substring(3, 5).toInt() in 0..59) { "Confira o horário do atendimento." }
        val values = ContentValues().apply {
            put("title", title.trim())
            put("date", date)
            put("time", time)
            put("location", location.trim())
            put("notes", notes.trim())
            put("kind", kind)
        }
        return if (id == null) writableDatabase.insertOrThrow("appointments", null, values)
        else {
            writableDatabase.update("appointments", values, "id=?", arrayOf(id.toString()))
            id
        }
    }

    @Synchronized
    fun deleteAppointment(id: Long) {
        writableDatabase.delete("appointments", "id=?", arrayOf(id.toString()))
    }

    @Synchronized
    fun students(): List<Student> {
        val result = mutableListOf<Student>()
        readableDatabase.query(
            "students",
            arrayOf("id", "name", "phone", "email", "birth_date"),
            null, null, null, null, "name COLLATE NOCASE ASC, id ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += Student(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4)
                )
            }
        }
        return result
    }

    @Synchronized
    fun student(id: Long): Student? {
        readableDatabase.query(
            "students",
            arrayOf("id", "name", "phone", "email", "birth_date"),
            "id=?", arrayOf(id.toString()), null, null, null, "1"
        ).use { cursor ->
            if (!cursor.moveToFirst()) return null
            return Student(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4)
            )
        }
    }

    @Synchronized
    fun saveStudent(name: String, phone: String, email: String, birthDate: String): Long {
        val values = ContentValues().apply {
            put("name", name.trim())
            put("phone", phone.trim())
            put("email", email.trim())
            put("birth_date", birthDate.trim())
            put("created_at", System.currentTimeMillis().toString())
        }
        return writableDatabase.insertOrThrow("students", null, values)
    }

    @Synchronized
    fun deleteStudent(id: Long) {
        writableDatabase.delete("students", "id=?", arrayOf(id.toString()))
    }

    @Synchronized
    fun assessments(studentId: Long): List<Assessment> {
        val result = mutableListOf<Assessment>()
        readableDatabase.query(
            "assessments",
            arrayOf("id", "student_id", "date", "weight_kg", "height_cm", "waist_cm", "hip_cm", "notes", "body_fat_percent", "neck_cm", "chest_cm", "arm_cm", "thigh_cm", "sex", "age_years", "fold_triceps_mm", "fold_subscapular_mm", "fold_chest_mm", "fold_axillary_mm", "fold_suprailiac_mm", "fold_abdominal_mm", "fold_thigh_mm", "bia_body_fat_percent", "bia_lean_mass_kg", "bia_device", "photo_data_json", "context_event"),
            "student_id=?", arrayOf(studentId.toString()), null, null, "date DESC, id DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += Assessment(
                    id = cursor.getLong(0),
                    studentId = cursor.getLong(1),
                    date = cursor.getString(2),
                    weightKg = cursor.getDouble(3),
                    heightCm = cursor.getDouble(4),
                    waistCm = if (cursor.isNull(5)) null else cursor.getDouble(5),
                    hipCm = if (cursor.isNull(6)) null else cursor.getDouble(6),
                    notes = cursor.getString(7),
                    bodyFatPercent = if (cursor.isNull(8)) null else cursor.getDouble(8),
                    neckCm = if (cursor.isNull(9)) null else cursor.getDouble(9),
                    chestCm = if (cursor.isNull(10)) null else cursor.getDouble(10),
                    armCm = if (cursor.isNull(11)) null else cursor.getDouble(11),
                    thighCm = if (cursor.isNull(12)) null else cursor.getDouble(12),
                    sex = cursor.getString(13),
                    ageYears = if (cursor.isNull(14)) null else cursor.getInt(14),
                    tricepsFoldMm = if (cursor.isNull(15)) null else cursor.getDouble(15),
                    subscapularFoldMm = if (cursor.isNull(16)) null else cursor.getDouble(16),
                    chestFoldMm = if (cursor.isNull(17)) null else cursor.getDouble(17),
                    axillaryFoldMm = if (cursor.isNull(18)) null else cursor.getDouble(18),
                    suprailiacFoldMm = if (cursor.isNull(19)) null else cursor.getDouble(19),
                    abdominalFoldMm = if (cursor.isNull(20)) null else cursor.getDouble(20),
                    thighFoldMm = if (cursor.isNull(21)) null else cursor.getDouble(21),
                    bioimpedanceBodyFatPercent = if (cursor.isNull(22)) null else cursor.getDouble(22),
                    bioimpedanceLeanMassKg = if (cursor.isNull(23)) null else cursor.getDouble(23),
                    bioimpedanceDevice = cursor.getString(24),
                    photoData = decodePhotoData(cursor.getString(25)),
                    contextEvent = cursor.getString(26)
                )
            }
        }
        return result
    }

    @Synchronized
    fun allAssessments(): List<Assessment> {
        val result = mutableListOf<Assessment>()
        readableDatabase.query(
            "assessments",
            arrayOf("id", "student_id", "date", "weight_kg", "height_cm", "waist_cm", "hip_cm", "notes", "body_fat_percent", "neck_cm", "chest_cm", "arm_cm", "thigh_cm", "sex", "age_years", "fold_triceps_mm", "fold_subscapular_mm", "fold_chest_mm", "fold_axillary_mm", "fold_suprailiac_mm", "fold_abdominal_mm", "fold_thigh_mm", "bia_body_fat_percent", "bia_lean_mass_kg", "bia_device", "photo_data_json", "context_event"),
            null, null, null, null, "date DESC, id DESC"
        ).use { cursor ->
            while (cursor.moveToNext()) result += Assessment(
                id = cursor.getLong(0), studentId = cursor.getLong(1), date = cursor.getString(2),
                weightKg = cursor.getDouble(3), heightCm = cursor.getDouble(4),
                waistCm = if (cursor.isNull(5)) null else cursor.getDouble(5),
                hipCm = if (cursor.isNull(6)) null else cursor.getDouble(6), notes = cursor.getString(7),
                bodyFatPercent = if (cursor.isNull(8)) null else cursor.getDouble(8),
                neckCm = if (cursor.isNull(9)) null else cursor.getDouble(9),
                chestCm = if (cursor.isNull(10)) null else cursor.getDouble(10),
                armCm = if (cursor.isNull(11)) null else cursor.getDouble(11),
                thighCm = if (cursor.isNull(12)) null else cursor.getDouble(12),
                sex = cursor.getString(13),
                ageYears = if (cursor.isNull(14)) null else cursor.getInt(14),
                tricepsFoldMm = if (cursor.isNull(15)) null else cursor.getDouble(15),
                subscapularFoldMm = if (cursor.isNull(16)) null else cursor.getDouble(16),
                chestFoldMm = if (cursor.isNull(17)) null else cursor.getDouble(17),
                axillaryFoldMm = if (cursor.isNull(18)) null else cursor.getDouble(18),
                suprailiacFoldMm = if (cursor.isNull(19)) null else cursor.getDouble(19),
                abdominalFoldMm = if (cursor.isNull(20)) null else cursor.getDouble(20),
                thighFoldMm = if (cursor.isNull(21)) null else cursor.getDouble(21),
                bioimpedanceBodyFatPercent = if (cursor.isNull(22)) null else cursor.getDouble(22),
                bioimpedanceLeanMassKg = if (cursor.isNull(23)) null else cursor.getDouble(23),
                bioimpedanceDevice = cursor.getString(24),
                photoData = decodePhotoData(cursor.getString(25)),
                    contextEvent = cursor.getString(26)
            )
        }
        return result
    }

    @Synchronized
    fun saveAssessment(
        studentId: Long,
        date: String,
        weightKg: Double,
        heightCm: Double,
        waistCm: Double?,
        hipCm: Double?,
        notes: String,
        bodyFatPercent: Double? = null,
        neckCm: Double? = null,
        chestCm: Double? = null,
        armCm: Double? = null,
        thighCm: Double? = null,
        id: Long? = null,
        sex: String = "Masculino",
        ageYears: Int? = null,
        tricepsFoldMm: Double? = null,
        subscapularFoldMm: Double? = null,
        chestFoldMm: Double? = null,
        axillaryFoldMm: Double? = null,
        suprailiacFoldMm: Double? = null,
        abdominalFoldMm: Double? = null,
        thighFoldMm: Double? = null,
        bioimpedanceBodyFatPercent: Double? = null,
        bioimpedanceLeanMassKg: Double? = null,
        bioimpedanceDevice: String = "",
        photoData: List<String> = emptyList(),
        contextEvent: String = ""
    ): Long {
        require(student(studentId) != null) { "Aluno não encontrado." }
        require(validDate(date)) { "Informe uma data válida para a avaliação." }
        require(AssessmentMath.bmi(weightKg, heightCm) != null) { "Confira peso e altura." }
        require(bodyFatPercent == null || bodyFatPercent in 1.0..70.0) { "Confira o percentual de gordura." }
        require(listOf(neckCm, chestCm, armCm, thighCm).all { it == null || it in 1.0..300.0 }) { "Confira as medidas opcionais." }
        require(sex in listOf("Masculino", "Feminino")) { "Escolha o sexo para a avaliação." }
        require(ageYears == null || ageYears in 18..100) { "Confira a idade da avaliação." }
        require(listOf(tricepsFoldMm, subscapularFoldMm, chestFoldMm, axillaryFoldMm, suprailiacFoldMm, abdominalFoldMm, thighFoldMm).all { it == null || it in 1.0..100.0 }) { "Confira as dobras cutâneas (1 a 100 mm)." }
        require(bioimpedanceBodyFatPercent == null || bioimpedanceBodyFatPercent in 1.0..70.0) { "Confira a gordura corporal informada pela bioimpedância." }
        require(bioimpedanceLeanMassKg == null || bioimpedanceLeanMassKg in 2.0..500.0) { "Confira a massa livre de gordura da bioimpedância." }
        require(bioimpedanceDevice.length <= 120) { "Informe um nome de aparelho com até 120 caracteres." }
        require(contextEvent in listOf("", "Início de treino", "Interrupção de treino", "Início de plano alimentar", "Interrupção de plano alimentar")) { "Evento de acompanhamento inválido." }
        require(photoData.size <= 4 && photoData.all { it.startsWith("data:image/jpeg;base64,") && it.length <= 1_500_000 }) { "Anexe até quatro fotos otimizadas por avaliação." }
        val values = ContentValues().apply {
            put("student_id", studentId)
            put("date", date)
            put("weight_kg", weightKg)
            put("height_cm", heightCm)
            if (waistCm == null) putNull("waist_cm") else put("waist_cm", waistCm)
            if (hipCm == null) putNull("hip_cm") else put("hip_cm", hipCm)
            if (bodyFatPercent == null) putNull("body_fat_percent") else put("body_fat_percent", bodyFatPercent)
            if (neckCm == null) putNull("neck_cm") else put("neck_cm", neckCm)
            if (chestCm == null) putNull("chest_cm") else put("chest_cm", chestCm)
            if (armCm == null) putNull("arm_cm") else put("arm_cm", armCm)
            if (thighCm == null) putNull("thigh_cm") else put("thigh_cm", thighCm)
            put("sex", sex)
            if (ageYears == null) putNull("age_years") else put("age_years", ageYears)
            listOf(
                "fold_triceps_mm" to tricepsFoldMm, "fold_subscapular_mm" to subscapularFoldMm,
                "fold_chest_mm" to chestFoldMm, "fold_axillary_mm" to axillaryFoldMm,
                "fold_suprailiac_mm" to suprailiacFoldMm, "fold_abdominal_mm" to abdominalFoldMm,
                "fold_thigh_mm" to thighFoldMm
            ).forEach { (column, value) -> if (value == null) putNull(column) else put(column, value) }
            if (bioimpedanceBodyFatPercent == null) putNull("bia_body_fat_percent") else put("bia_body_fat_percent", bioimpedanceBodyFatPercent)
            if (bioimpedanceLeanMassKg == null) putNull("bia_lean_mass_kg") else put("bia_lean_mass_kg", bioimpedanceLeanMassKg)
            put("bia_device", bioimpedanceDevice.trim())
            put("photo_data_json", JSONArray().apply { photoData.forEach { put(it) } }.toString())
            put("context_event", contextEvent)
            put("notes", notes.trim())
        }
        return if (id == null) writableDatabase.insertOrThrow("assessments", null, values)
        else {
            require(writableDatabase.update("assessments", values, "id=? AND student_id=?", arrayOf(id.toString(), studentId.toString())) == 1) { "Avaliação não encontrada para este aluno." }
            id
        }
    }

    private fun decodePhotoData(json: String): List<String> = runCatching {
        val array = JSONArray(json)
        List(array.length()) { array.getString(it) }
    }.getOrDefault(emptyList())

    @Synchronized
    fun deleteAssessment(id: Long) {
        writableDatabase.delete("assessments", "id=?", arrayOf(id.toString()))
    }


    @Synchronized
    fun updateStudent(id: Long, name: String, phone: String, email: String, birthDate: String) {
        require(name.isNotBlank()) { "Informe o nome do aluno." }
        val values = ContentValues().apply {
            put("name", name.trim())
            put("phone", phone.trim())
            put("email", email.trim())
            put("birth_date", birthDate.trim())
        }
        require(writableDatabase.update("students", values, "id=?", arrayOf(id.toString())) == 1) { "Aluno não encontrado." }
    }

    @Synchronized
    fun tasks(): List<TaskRecord> = readableDatabase.query(
        "tasks", arrayOf("id", "title", "due_date", "completed"), null, null, null, null,
        "completed ASC, due_date ASC, id DESC"
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(TaskRecord(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getInt(3) != 0))
        }
    }

    @Synchronized
    fun saveTask(id: Long?, title: String, dueDate: String, completed: Boolean = false): Long {
        require(title.isNotBlank()) { "Informe a tarefa." }
        require(validDate(dueDate)) { "Informe uma data válida." }
        val values = ContentValues().apply { put("title", title.trim()); put("due_date", dueDate); put("completed", if (completed) 1 else 0) }
        return if (id == null) writableDatabase.insertOrThrow("tasks", null, values)
        else { writableDatabase.update("tasks", values, "id=?", arrayOf(id.toString())); id }
    }

    @Synchronized
    fun setTaskCompleted(id: Long, completed: Boolean) {
        writableDatabase.update("tasks", ContentValues().apply { put("completed", if (completed) 1 else 0) }, "id=?", arrayOf(id.toString()))
    }

    @Synchronized
    fun deleteTask(id: Long) { writableDatabase.delete("tasks", "id=?", arrayOf(id.toString())) }

    @Synchronized
    fun intakeForms(studentId: Long? = null): List<IntakeForm> = readableDatabase.query(
        "intake_forms", arrayOf("id", "student_id", "template", "date", "responses"),
        if (studentId == null) null else "student_id=?", if (studentId == null) null else arrayOf(studentId.toString()),
        null, null, "date DESC, id DESC"
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(IntakeForm(cursor.getLong(0), if (cursor.isNull(1)) null else cursor.getLong(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)))
        }
    }

    @Synchronized
    fun saveIntakeForm(studentId: Long?, template: String, date: String, responses: String, id: Long? = null): Long {
        require(template.isNotBlank() && responses.isNotBlank()) { "Escolha um modelo e preencha as respostas." }
        require(validDate(date)) { "Informe uma data válida para o formulário." }
        if (studentId != null) require(student(studentId) != null) { "Aluno não encontrado." }
        val values = ContentValues().apply {
            if (studentId == null) putNull("student_id") else put("student_id", studentId)
            put("template", template); put("date", date); put("responses", responses.trim())
        }
        return if (id == null) writableDatabase.insertOrThrow("intake_forms", null, values)
        else {
            require(writableDatabase.update("intake_forms", values, "id=?", arrayOf(id.toString())) == 1) { "Formulário não encontrado." }
            id
        }
    }

    @Synchronized
    fun deleteIntakeForm(id: Long) { writableDatabase.delete("intake_forms", "id=?", arrayOf(id.toString())) }

    @Synchronized
    fun charges(): List<ChargeRecord> = readableDatabase.query(
        "charges", arrayOf("id", "student_id", "title", "amount", "due_date", "status", "notes"),
        null, null, null, null, "CASE status WHEN 'Pendente' THEN 0 ELSE 1 END, due_date ASC, id DESC"
    ).use { cursor ->
        buildList {
            while (cursor.moveToNext()) add(ChargeRecord(cursor.getLong(0), if (cursor.isNull(1)) null else cursor.getLong(1), cursor.getString(2), cursor.getDouble(3), cursor.getString(4), cursor.getString(5), cursor.getString(6)))
        }
    }

    @Synchronized
    fun saveCharge(studentId: Long?, title: String, amount: Double, dueDate: String, notes: String, id: Long? = null): Long {
        require(title.isNotBlank() && amount.isFinite() && amount > 0.0) { "Informe descrição e valor finito maior que zero." }
        require(validDate(dueDate)) { "Informe uma data válida." }
        if (studentId != null) require(student(studentId) != null) { "Aluno não encontrado." }
        val values = ContentValues().apply {
            if (studentId == null) putNull("student_id") else put("student_id", studentId)
            put("title", title.trim()); put("amount", amount); put("due_date", dueDate); put("notes", notes.trim())
            if (id == null) put("status", "Pendente")
        }
        return if (id == null) writableDatabase.insertOrThrow("charges", null, values)
        else {
            require(writableDatabase.update("charges", values, "id=?", arrayOf(id.toString())) == 1) { "Cobrança não encontrada." }
            id
        }
    }

    @Synchronized
    fun markChargeReceived(id: Long) {
        writableDatabase.update("charges", ContentValues().apply { put("status", "Recebido") }, "id=?", arrayOf(id.toString()))
    }

    @Synchronized
    fun deleteCharge(id: Long) { writableDatabase.delete("charges", "id=?", arrayOf(id.toString())) }

    @Synchronized
    fun pendingChargeCount(): Int = readableDatabase.rawQuery("SELECT COUNT(*) FROM charges WHERE status='Pendente'", null)
        .use { if (it.moveToFirst()) it.getInt(0) else 0 }

    @Synchronized
    fun exportBackupJson(): String {
        val db = readableDatabase
        val root = JSONObject().put("format", "evolufisico-native-backup").put("schema", 6)
        fun rows(table: String, columns: Array<String>): JSONArray {
            val result = JSONArray()
            db.query(table, columns, null, null, null, null, null).use { cursor ->
                while (cursor.moveToNext()) {
                    val row = JSONObject()
                    columns.forEachIndexed { index, column ->
                        if (cursor.isNull(index)) row.put(column, JSONObject.NULL)
                        else when (cursor.getType(index)) {
                            android.database.Cursor.FIELD_TYPE_INTEGER -> row.put(column, cursor.getLong(index))
                            android.database.Cursor.FIELD_TYPE_FLOAT -> row.put(column, cursor.getDouble(index))
                            else -> row.put(column, cursor.getString(index))
                        }
                    }
                    result.put(row)
                }
            }
            return result
        }
        root.put("students", rows("students", arrayOf("id","name","phone","email","birth_date","created_at")))
        root.put("assessments", rows("assessments", arrayOf("id","student_id","date","weight_kg","height_cm","waist_cm","hip_cm","notes","body_fat_percent","neck_cm","chest_cm","arm_cm","thigh_cm","sex","age_years","fold_triceps_mm","fold_subscapular_mm","fold_chest_mm","fold_axillary_mm","fold_suprailiac_mm","fold_abdominal_mm","fold_thigh_mm","bia_body_fat_percent","bia_lean_mass_kg","bia_device","photo_data_json","context_event")))
        root.put("appointments", rows("appointments", arrayOf("id","title","date","time","location","notes","kind")))
        root.put("tasks", rows("tasks", arrayOf("id","title","due_date","completed")))
        root.put("intake_forms", rows("intake_forms", arrayOf("id","student_id","template","date","responses")))
        root.put("charges", rows("charges", arrayOf("id","student_id","title","amount","due_date","status","notes")))
        return root.toString(2)
    }

    @Synchronized
    fun restoreBackupJson(json: String) {
        BackupInspector.inspect(json) // validate before deleting even a single row
        val root = JSONObject(json)
        val db = writableDatabase
        db.beginTransaction()
        try {
            listOf("assessments", "intake_forms", "charges", "appointments", "tasks", "students").forEach { db.delete(it, null, null) }
            insertRows(db, root.getJSONArray("students"), "students")
            insertRows(db, root.optJSONArray("assessments") ?: JSONArray(), "assessments")
            insertRows(db, root.optJSONArray("appointments") ?: JSONArray(), "appointments")
            insertRows(db, root.optJSONArray("tasks") ?: JSONArray(), "tasks")
            insertRows(db, root.optJSONArray("intake_forms") ?: JSONArray(), "intake_forms")
            insertRows(db, root.optJSONArray("charges") ?: JSONArray(), "charges")
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    private fun hasColumn(db: SQLiteDatabase, table: String, column: String): Boolean =
        db.rawQuery("PRAGMA table_info($table)", null).use { cursor ->
            var found = false
            while (cursor.moveToNext()) {
                if (cursor.getString(1) == column) found = true
            }
            found
        }

    private fun validDate(value: String): Boolean = runCatching {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT).apply { isLenient = false }
        formatter.parse(value)?.let { formatter.format(it) == value } == true
    }.getOrDefault(false)

    private fun insertRows(db: SQLiteDatabase, array: JSONArray, table: String) {
        for (i in 0 until array.length()) {
            val row = array.getJSONObject(i)
            val values = ContentValues()
            val keys = row.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                when (val value = row.get(key)) {
                    JSONObject.NULL -> values.putNull(key)
                    is Int -> values.put(key, value)
                    is Long -> values.put(key, value)
                    is Double -> values.put(key, value)
                    is Number -> values.put(key, value.toDouble())
                    else -> values.put(key, value.toString())
                }
            }
            db.insertOrThrow(table, null, values)
        }
    }

    @Synchronized
    fun studentsWithoutAssessment(): Int =
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM students s WHERE NOT EXISTS (SELECT 1 FROM assessments a WHERE a.student_id=s.id)",
            null
        ).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    @Synchronized
    fun reevaluationsDue(days: Int = 90): Int =
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM (SELECT student_id, MAX(date) latest FROM assessments GROUP BY student_id) WHERE julianday('now') - julianday(latest) >= ?",
            arrayOf(days.toString())
        ).use { if (it.moveToFirst()) it.getInt(0) else 0 }

    @Synchronized
    fun counts(): Pair<Int, Int> {
        val studentCount = readableDatabase.rawQuery("SELECT COUNT(*) FROM students", null).use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
        val assessmentCount = readableDatabase.rawQuery("SELECT COUNT(*) FROM assessments", null).use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
        return studentCount to assessmentCount
    }
}
