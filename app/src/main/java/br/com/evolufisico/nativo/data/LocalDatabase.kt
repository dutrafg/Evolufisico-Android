package br.com.evolufisico.nativo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
    val notes: String
)

data class Assessment(
    val id: Long,
    val studentId: Long,
    val date: String,
    val weightKg: Double,
    val heightCm: Double,
    val waistCm: Double?,
    val hipCm: Double?,
    val notes: String
) {
    val bmi: Double? get() = AssessmentMath.bmi(weightKg, heightCm)
}

class LocalDatabase(context: Context) :
    SQLiteOpenHelper(context.applicationContext, "evolufisico-native.db", null, 2) {

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
                notes TEXT NOT NULL DEFAULT ''
            )"""
        )
        db.execSQL("CREATE INDEX index_assessments_student_date ON assessments(student_id, date DESC, id DESC)")
        db.execSQL("CREATE INDEX index_students_name ON students(name COLLATE NOCASE)")
        createAppointments(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) createAppointments(db)
    }

    private fun createAppointments(db: SQLiteDatabase) {
        db.execSQL(
            """CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                date TEXT NOT NULL,
                time TEXT NOT NULL,
                location TEXT NOT NULL DEFAULT '',
                notes TEXT NOT NULL DEFAULT ''
            )"""
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS index_appointments_date_time ON appointments(date, time)")
    }

    @Synchronized
    fun appointments(date: String? = null): List<Appointment> {
        val result = mutableListOf<Appointment>()
        readableDatabase.query(
            "appointments",
            arrayOf("id", "title", "date", "time", "location", "notes"),
            if (date == null) null else "date=?",
            if (date == null) null else arrayOf(date),
            null, null, "date ASC, time ASC, id ASC"
        ).use { cursor ->
            while (cursor.moveToNext()) {
                result += Appointment(
                    cursor.getLong(0), cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5)
                )
            }
        }
        return result
    }

    @Synchronized
    fun saveAppointment(title: String, date: String, time: String, location: String, notes: String): Long {
        require(title.isNotBlank()) { "Informe o motivo do atendimento." }
        require(date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) { "Confira a data do atendimento." }
        require(time.matches(Regex("\\d{2}:\\d{2}"))) { "Confira o horário do atendimento." }
        val values = ContentValues().apply {
            put("title", title.trim())
            put("date", date)
            put("time", time)
            put("location", location.trim())
            put("notes", notes.trim())
        }
        return writableDatabase.insertOrThrow("appointments", null, values)
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
            arrayOf("id", "student_id", "date", "weight_kg", "height_cm", "waist_cm", "hip_cm", "notes"),
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
                    notes = cursor.getString(7)
                )
            }
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
        notes: String
    ): Long {
        require(student(studentId) != null) { "Aluno não encontrado." }
        require(AssessmentMath.bmi(weightKg, heightCm) != null) { "Confira peso e altura." }
        val values = ContentValues().apply {
            put("student_id", studentId)
            put("date", date)
            put("weight_kg", weightKg)
            put("height_cm", heightCm)
            if (waistCm == null) putNull("waist_cm") else put("waist_cm", waistCm)
            if (hipCm == null) putNull("hip_cm") else put("hip_cm", hipCm)
            put("notes", notes.trim())
        }
        return writableDatabase.insertOrThrow("assessments", null, values)
    }

    @Synchronized
    fun deleteAssessment(id: Long) {
        writableDatabase.delete("assessments", "id=?", arrayOf(id.toString()))
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
