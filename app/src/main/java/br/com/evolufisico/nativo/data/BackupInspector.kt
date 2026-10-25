package br.com.evolufisico.nativo.data

import org.json.JSONArray
import org.json.JSONObject

/**
 * Non-destructive preflight for Android-local backup restoration.
 * Enforces format, supported schema, structural limits, duplicate IDs and student references.
 * It never opens a writable database and therefore cannot alter current records.
 */
object BackupInspector {
    const val FORMAT = "evolufisico-native-backup"
    const val LATEST_SCHEMA = 6
    private const val MAX_ROWS_PER_TABLE = 100_000
    private const val MAX_JSON_CHARS = 200_000_000

    data class Summary(
        val schema: Int,
        val students: Int,
        val assessments: Int,
        val appointments: Int,
        val tasks: Int,
        val forms: Int,
        val charges: Int
    ) {
        val totalRecords: Int get() = students + assessments + appointments + tasks + forms + charges
        fun asText(): String =
            "Versão do backup: $schema. Alunos: $students, avaliações: $assessments, " +
                "agenda: $appointments, tarefas: $tasks, anamneses: $forms, cobranças: $charges."
    }

    fun inspect(json: String): Summary {
        require(json.length <= MAX_JSON_CHARS) { "Arquivo de backup grande demais para importar com segurança." }
        val root = try { JSONObject(json) } catch (_: Exception) {
            throw IllegalArgumentException("O arquivo selecionado não contém JSON válido.")
        }
        require(root.optString("format") == FORMAT) { "Formato do arquivo incompatível." }
        val schema = root.optInt("schema", -1)
        require(schema in 1..LATEST_SCHEMA) { "Versão do backup incompatível ou futura." }
        val tableNames = listOf("students", "assessments", "appointments", "tasks", "intake_forms", "charges")
        val arrays = tableNames.associateWith { table ->
            val value = root.opt(table)
            when {
                table == "students" && value !is JSONArray -> throw IllegalArgumentException("Faltam os alunos no backup.")
                value == null -> JSONArray()
                value is JSONArray -> value
                else -> throw IllegalArgumentException("Estrutura inválida: $table.")
            }
        }
        val idSets = mutableMapOf<String, Set<Long>>()
        arrays.forEach { (name, array) ->
            require(array.length() <= MAX_ROWS_PER_TABLE) { "Quantidade de registros fora do limite: $name." }
            val ids = mutableSetOf<Long>()
            for (i in 0 until array.length()) {
                val row = array.optJSONObject(i)
                    ?: throw IllegalArgumentException("Registro inválido em $name, posição ${i + 1}.")
                val id = row.optLong("id", 0L)
                require(id > 0 && ids.add(id)) { "Identificador inválido ou duplicado em $name." }
            }
            idSets[name] = ids
        }
        val studentIds = idSets.getValue("students")
        for (name in listOf("assessments", "intake_forms", "charges")) {
            val array = arrays.getValue(name)
            for (i in 0 until array.length()) {
                val row = array.getJSONObject(i)
                val link = row.opt("student_id")
                if (name == "assessments") {
                    require(link is Number && link.toLong() in studentIds) {
                        "Avaliação vinculada a aluno ausente no backup."
                    }
                } else if (link != null && link != JSONObject.NULL) {
                    require(link is Number && link.toLong() in studentIds) {
                        "Registro em $name vinculado a aluno ausente."
                    }
                }
            }
        }
        return Summary(
            schema,
            arrays.getValue("students").length(),
            arrays.getValue("assessments").length(),
            arrays.getValue("appointments").length(),
            arrays.getValue("tasks").length(),
            arrays.getValue("intake_forms").length(),
            arrays.getValue("charges").length()
        )
    }
}
