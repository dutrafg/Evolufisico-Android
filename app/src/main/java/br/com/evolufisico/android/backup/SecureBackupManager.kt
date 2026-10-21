package br.com.evolufisico.android.backup

import android.content.Context
import br.com.evolufisico.android.billing.Charge
import br.com.evolufisico.android.billing.ChargeJson
import br.com.evolufisico.android.billing.ChargeRules
import br.com.evolufisico.android.billing.EncryptedChargeStore
import br.com.evolufisico.android.data.Assessment
import br.com.evolufisico.android.data.AssessmentRules
import br.com.evolufisico.android.data.Client
import br.com.evolufisico.android.data.ClientRules
import br.com.evolufisico.android.data.EncryptedAssessmentStore
import br.com.evolufisico.android.data.EncryptedClientStore
import br.com.evolufisico.android.forms.EncryptedFormStore
import br.com.evolufisico.android.forms.FormResponse
import br.com.evolufisico.android.forms.FormsJson
import br.com.evolufisico.android.forms.FormRules
import br.com.evolufisico.android.ops.OperationalStore
import br.com.evolufisico.android.ops.OperationsData
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.time.Instant

data class BackupSnapshot(
    val clients: List<Client>,
    val assessments: List<Assessment>,
    val charges: List<Charge>,
    val forms: List<FormResponse> = emptyList(),
    val operations: OperationsData = OperationsData()
) {
    val recordCount: Int get() = clients.size + assessments.size + charges.size + forms.size +
        operations.appointments.size + operations.goals.size +
        operations.services.size + operations.notes.size
}

/**
 * Backup em JSON legível para a fase alpha. Continua reconhecendo os backups
 * AES-GCM antigos, exigindo senha apenas nesses casos.
 * Importação somente numa base vazia evita sobrepor registros.
 */
class SecureBackupManager(context: Context) {
    private val journal = File(context.applicationContext.filesDir, "restore_transaction.pending")
    private val clientStore = EncryptedClientStore(context.applicationContext)
    private val assessmentStore = EncryptedAssessmentStore(context.applicationContext)
    private val chargeStore = EncryptedChargeStore(context.applicationContext)
    private val formStore = EncryptedFormStore(context.applicationContext)
    private val operationsStore = OperationalStore(context.applicationContext)

    /**
     * Se o processo morreu no meio da restauração, limpa os dados parciais.
     * O diário só é criado após verificar que os três bancos estavam vazios.
     * Uma falha de limpeza mantém o diário e impede abertura dos dados.
     */
    @Synchronized
    fun recoverInterruptedRestore() {
        if (!journal.exists()) return
        require(journal.readText(Charsets.US_ASCII) == "EF_RESTORE_V1") {
            "Estado de restauração inesperado. Dados protegidos."
        }
        operationsStore.save(OperationsData(),emptySet())
        formStore.save(emptyList(), emptySet())
        chargeStore.save(emptyList(), emptySet())
        assessmentStore.save(emptyList())
        clientStore.save(emptyList())
        check(journal.delete()) { "Falha ao finalizar recuperação da restauração." }
    }

    @Synchronized
    fun export(password: CharArray): ByteArray {
        check(!journal.exists()) { "Recuperação de restauração pendente." }
        val snapshot = BackupSnapshot(
            clientStore.load(), assessmentStore.load(), chargeStore.load(),
            formStore.load(), operationsStore.load()
        )
        validate(snapshot)
        val json = JSONObject()
            .put("schema", 1).put("app", "EvoluFisico-Android")
            .put("createdAt", Instant.now().toString())
            .put("clients", JSONArray().apply {
                snapshot.clients.forEach {
                    put(JSONObject().put("id",it.id).put("name",it.name)
                        .put("phone",it.phone).put("email",it.email)
                        .put("birthDateIso",it.birthDateIso))
                }
            })
            .put("assessments", JSONArray().apply {
                snapshot.assessments.forEach { a ->
                    put(JSONObject().put("id", a.id).put("clientId", a.clientId)
                        .put("dateIso", a.dateIso).put("trainingLevel", a.trainingLevel)
                        .put("goal", a.goal).put("limitations", a.limitations)
                        .put("weeklyFrequency", a.weeklyFrequency)
                        .put("weightKg", a.weightKg).put("heightCm", a.heightCm)
                        .put("waistCm", a.waistCm ?: JSONObject.NULL)
                        .put("hipCm", a.hipCm ?: JSONObject.NULL)
                        .put("armCm", a.armCm ?: JSONObject.NULL)
                        .put("thighCm", a.thighCm ?: JSONObject.NULL)
                        .put("healthHistory", a.healthHistory)
                        .put("dailyHabits", a.dailyHabits)
                        .put("trainingExperience", a.trainingExperience)
                        .put("chestCm",a.chestCm?:JSONObject.NULL)
                        .put("abdomenCm",a.abdomenCm?:JSONObject.NULL)
                        .put("calfCm",a.calfCm?:JSONObject.NULL)
                        .put("forearmCm",a.forearmCm?:JSONObject.NULL))
                }
            })
            .put("charges", ChargeJson.toArray(snapshot.charges))
            .put("forms", FormsJson.toArray(snapshot.forms))
            .put("operations", OperationalStore.encode(snapshot.operations))
        return json.toString().toByteArray(Charsets.UTF_8)
    }

    /**
     * Descriptografa e valida tudo ANTES de alterar o banco.
     * NUNCA mistura dados nem sobrescreve base preexistente.
     */
    /**
     * Verifica um arquivo .efbak SEM escrever em qualquer banco.
     * Permite conferir o backup antes de atualizar ou reinstalar.
     */
    @Synchronized
    fun inspectBackup(input: InputStream, password: CharArray): BackupSnapshot {
        check(!journal.exists()) { "Há restauração pendente de recuperação." }
        val encrypted = input.use { source ->
            val target = ByteArrayOutputStream()
            val buffer = ByteArray(8192)
            while (true) {
                val read = source.read(buffer)
                if (read < 0) break
                target.write(buffer, 0, read)
                require(target.size() <= 16 * 1024 * 1024) {
                    "Arquivo de backup excede 16 MB."
                }
            }
            target.toByteArray()
        }
        // Arquivos novos são JSON simples. .efbak antigos continuam legíveis
        // mediante a senha original; nunca restaurar sobre registros existentes.
        val plain = if (encrypted.isNotEmpty() && encrypted[0] == '{'.code.toByte())
            encrypted else PortableBackupCrypto.decrypt(encrypted, password)
        try {
            return parse(String(plain, Charsets.UTF_8)).also { validate(it) }
        } finally { plain.fill(0); encrypted.fill(0) }
    }

    @Synchronized
    fun restore(input: InputStream, password: CharArray): BackupSnapshot {
        check(!journal.exists()) { "Restauração anterior pendente. Reabra o aplicativo para recuperá-la." }
        val snapshot = inspectBackup(input, password)
        require(clientStore.load().isEmpty() && assessmentStore.load().isEmpty()
            && chargeStore.load().isEmpty() && formStore.load().isEmpty() &&
            operationsStore.load() == OperationsData()) {
            "Restauração permitida apenas numa base vazia. Nenhum dado foi sobrescrito."
        }
        FileOutputStream(journal).use { out ->
            out.write("EF_RESTORE_V1".toByteArray(Charsets.US_ASCII))
            out.fd.sync()
        }
        try {
            clientStore.save(snapshot.clients)
            assessmentStore.save(snapshot.assessments)
            chargeStore.save(snapshot.charges, snapshot.clients.map { it.id }.toSet())
            formStore.save(snapshot.forms, snapshot.clients.map { it.id }.toSet())
            operationsStore.save(snapshot.operations, snapshot.clients.map { it.id }.toSet())
            check(journal.delete()) { "Falha ao concluir a transação de restauração." }
        } catch (error: Exception) {
            val failures = listOf(
                runCatching { operationsStore.save(OperationsData(), emptySet()) },
                runCatching { formStore.save(emptyList(), emptySet()) },
                runCatching { chargeStore.save(emptyList(), emptySet()) },
                runCatching { assessmentStore.save(emptyList()) },
                runCatching { clientStore.save(emptyList()) }
            ).mapNotNull { it.exceptionOrNull() }
            failures.forEach { error.addSuppressed(it) }
            if (failures.isEmpty()) {
                if (!journal.delete()) {
                    throw IllegalStateException(
                        "Dados parciais removidos, mas o diário não pôde ser removido.", error
                    )
                }
            }
            if (failures.isNotEmpty()) {
                throw IllegalStateException(
                    "Falha ao restaurar e reverter os dados locais. Preserve o backup e procure suporte.",
                    error
                )
            }
            throw IllegalStateException("Restauração interrompida; os registros anteriores estavam vazios.", error)
        }
        return snapshot
    }

    @Synchronized
    fun summary(): BackupSnapshot {
        check(!journal.exists()) { "Recuperação de restauração pendente." }
        return BackupSnapshot(clientStore.load(), assessmentStore.load(), chargeStore.load(),
            formStore.load(), operationsStore.load()).also { validate(it) }
    }

    private fun parse(plaintext: String): BackupSnapshot {
        val root = JSONObject(plaintext)
        require(root.getString("app") == "EvoluFisico-Android" && root.getInt("schema") == 1) {
            "Formato de backup incompatível."
        }
        val clientsArray = root.getJSONArray("clients")
        val assessmentArray = root.getJSONArray("assessments")
        require(clientsArray.length() <= 20000 && assessmentArray.length() <= 20000)
        val clients = (0 until clientsArray.length()).map { i ->
            val entry = clientsArray.getJSONObject(i)
            Client(entry.getString("id"),entry.getString("name"),
                entry.optString("phone",""),entry.optString("email",""),
                entry.optString("birthDateIso",""))
        }
        val assessments = (0 until assessmentArray.length()).map { i ->
            val j = assessmentArray.getJSONObject(i)
            fun opt(name: String): Double? =
                if (j.isNull(name)) null else j.getDouble(name)
            Assessment(j.getString("id"), j.getString("clientId"),
                j.getString("dateIso"), j.getString("trainingLevel"),
                j.getString("goal"), j.getString("limitations"),
                j.getInt("weeklyFrequency"), j.getDouble("weightKg"),
                j.getDouble("heightCm"), opt("waistCm"), opt("hipCm"),
                opt("armCm"), opt("thighCm"),
                j.optString("healthHistory", ""), j.optString("dailyHabits", ""),
                j.optString("trainingExperience", ""),
                opt("chestCm"),opt("abdomenCm"),
                opt("calfCm"),opt("forearmCm"))
        }
        val forms = if (root.has("forms") && !root.isNull("forms"))
            FormsJson.fromArray(root.getJSONArray("forms")) else emptyList()
        val ops = if (root.has("operations") && !root.isNull("operations"))
            OperationalStore.decode(root.getJSONObject("operations")) else OperationsData()
        return BackupSnapshot(clients, assessments,
            ChargeJson.fromArray(root.getJSONArray("charges")), forms, ops)
    }

    private fun validate(snapshot: BackupSnapshot) {
        val structureError = IntegrityReport.inspect(
            snapshot.clients, snapshot.assessments, snapshot.charges
        )
        require(structureError == null) { structureError ?: "Dados inconsistentes." }
        require(snapshot.clients.size <= 20000 && snapshot.assessments.size <= 20000
            && snapshot.charges.size <= 20000 && snapshot.forms.size <= 20000)
        val ids = snapshot.clients.map { it.id }.toSet()
        require(ids.size == snapshot.clients.size)
        require(snapshot.clients.all { it.id.isNotBlank() &&
            ClientRules.validateContact(it) == null }) { "Clientes inválidos no backup." }
        require(snapshot.assessments.distinctBy { it.id }.size == snapshot.assessments.size)
        require(snapshot.assessments.all { it.id.isNotBlank() &&
            AssessmentRules.validate(it, ids) == null }) { "Avaliações inválidas no backup." }
        require(snapshot.charges.distinctBy { it.id }.size == snapshot.charges.size)
        require(snapshot.charges.all { ChargeRules.validate(it, ids) == null }) {
            "Cobranças inválidas no backup."
        }
        require(snapshot.forms.distinctBy { it.id }.size == snapshot.forms.size)
        require(snapshot.forms.all { FormRules.validate(it, ids) == null }) {
            "Formulários inválidos no backup."
        }
        OperationalStore.validate(snapshot.operations, ids)
    }
}
