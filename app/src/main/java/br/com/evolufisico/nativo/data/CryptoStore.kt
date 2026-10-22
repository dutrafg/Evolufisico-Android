package br.com.evolufisico.nativo.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import br.com.evolufisico.nativo.domain.WorkspaceRules
import org.json.JSONArray
import org.json.JSONObject
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** SQL indexing only stores kind/id; JSON payloads are AES-GCM encrypted by Android Keystore. */
class CryptoStore(context: Context) : SQLiteOpenHelper(context, "evolufisico-native.db", null, 1) {
    private val keyAlias = "evolufisico-native-v1"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE records(kind TEXT NOT NULL,id TEXT NOT NULL,payload BLOB NOT NULL,PRIMARY KEY(kind,id))")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        throw IllegalStateException("Migração explícita do banco necessária")
    }
    private fun key(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(keyAlias, null) as? SecretKey)?.let { return it }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore").apply {
            init(KeyGenParameterSpec.Builder(keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256).build())
        }.generateKey()
    }
    private fun encrypt(data: ByteArray): ByteArray = Cipher.getInstance("AES/GCM/NoPadding").run {
        init(Cipher.ENCRYPT_MODE, key()); iv + doFinal(data)
    }
    private fun decrypt(data: ByteArray): ByteArray {
        require(data.size > 12) { "Envelope criptografado inválido" }
        return Cipher.getInstance("AES/GCM/NoPadding").run {
            init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, data.copyOfRange(0, 12)))
            doFinal(data.copyOfRange(12, data.size))
        }
    }
    private val allowedKinds = setOf("evaluation", "charge", "intake", "appointment", "task", "training", "habit", "note")
    fun put(kind: String, id: String, data: JSONObject) {
        require(kind in allowedKinds && id.isNotBlank() && id.length <= 128)
        val blob = encrypt(data.toString().toByteArray(Charsets.UTF_8))
        val values = ContentValues().apply { put("kind", kind); put("id", id); put("payload", blob) }
        check(writableDatabase.insertWithOnConflict("records", null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1L)
    }
    /** Encrypt first, then commit every occurrence in one SQLite transaction. */
    fun putAppointments(items: List<Appointment>) {
        require(items.isNotEmpty() && items.size <= 12)
        require(items.map { it.id }.distinct().size == items.size)
        val rows = items.map { item ->
            require(item.id.isNotBlank() && item.id.length <= 128)
            ContentValues().apply {
                put("kind", "appointment"); put("id", item.id)
                put("payload", encrypt(item.toJson().toString().toByteArray(Charsets.UTF_8)))
            }
        }
        val db = writableDatabase
        db.beginTransaction()
        try {
            rows.forEach { values ->
                check(db.insertWithOnConflict("records", null, values, SQLiteDatabase.CONFLICT_REPLACE) != -1L)
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
    }

    fun list(kind: String): List<JSONObject> {
        require(kind in allowedKinds)
        val list = mutableListOf<JSONObject>()
        readableDatabase.query("records", arrayOf("payload"), "kind=?", arrayOf(kind),
            null, null, "id DESC").use { c ->
            while (c.moveToNext()) list.add(JSONObject(String(decrypt(c.getBlob(0)), Charsets.UTF_8)))
        }
        return list
    }
    fun delete(kind: String, id: String) { require(kind in allowedKinds); writableDatabase.delete("records", "kind=? AND id=?", arrayOf(kind, id)) }

    /** Export is portable and passphrase-encrypted: Keystore device keys never leave the device. */
    fun exportEncrypted(passphrase: CharArray): ByteArray {
        require(passphrase.size >= 8) { "Senha deve conter pelo menos 8 caracteres" }
        val all = JSONArray()
        for (kind in allowedKinds) for (record in list(kind)) all.put(JSONObject().put("kind", kind).put("record", record))
        val plain = JSONObject().put("schema", 1).put("records", all).toString().toByteArray(Charsets.UTF_8)
        require(plain.size <= 16_000_000) { "Backup excede 16 MB" }
        val salt = ByteArray(16).also(SecureRandom()::nextBytes)
        val derived = derive(passphrase, salt)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply { init(Cipher.ENCRYPT_MODE, derived) }
        val output = JSONObject().put("type", "EF-NATIVE-BACKUP")
            .put("version", 1).put("iterations", 210000)
            .put("salt", b64(salt)).put("iv", b64(cipher.iv))
            .put("ciphertext", b64(cipher.doFinal(plain)))
        plain.fill(0)
        return output.toString().toByteArray(Charsets.UTF_8)
    }
    /** Import rejects conflicts, unknown kinds and malformed schemas; no existing rows are erased. */
    fun importEncrypted(file: ByteArray, passphrase: CharArray): Int {
        require(file.size < 24_000_000) { "Arquivo excessivamente grande" }
        val obj = JSONObject(String(file, Charsets.UTF_8))
        require(obj.getString("type") == "EF-NATIVE-BACKUP" && obj.getInt("version") == 1) { "Formato de backup incompatível" }
        require(obj.getInt("iterations") == 210000) { "Parâmetros criptográficos inválidos" }
        val salt = from64(obj.getString("salt")); val iv = from64(obj.getString("iv"))
        require(salt.size == 16 && iv.size == 12)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, derive(passphrase, salt), GCMParameterSpec(128, iv))
        val clear = cipher.doFinal(from64(obj.getString("ciphertext")))
        val payload = JSONObject(String(clear, Charsets.UTF_8)); clear.fill(0)
        require(payload.getInt("schema") == 1) { "Versão dos registros não suportada" }
        val arr = payload.getJSONArray("records")
        require(arr.length() <= 10000) { "Backup excede 10000 registros" }
        val staged = mutableListOf<Triple<String, String, JSONObject>>()
        val seen = HashSet<String>()
        for (i in 0 until arr.length()) {
            val entry = arr.getJSONObject(i); val kind = entry.getString("kind")
            require(kind in allowedKinds)
            val record = entry.getJSONObject("record")
            val id = record.getString("id")
            require(id.isNotBlank() && id.length <= 128 && seen.add("$kind:$id"))
            when (kind) {
                "evaluation" -> Evaluation.fromJson(record)
                "charge" -> Charge.fromJson(record)
                "intake" -> Intake.fromJson(record)
                "appointment" -> Appointment.fromJson(record)
                "task" -> TaskItem.fromJson(record).also(WorkspaceRules::validate)
                "training" -> WorkspaceRules.validate(TrainingSession.fromJson(record))
                "habit" -> WorkspaceRules.validate(HabitEntry.fromJson(record))
                "note" -> WorkspaceRules.validate(NotebookEntry.fromJson(record))
            }
            staged.add(Triple(kind, id, record))
        }
        return importStaged(staged)

    }
    /** All-or-nothing import; refuses collisions and never modifies pre-existing records. */
    fun importLegacy(stage: LegacyImporter.Stage): Int {
        require(stage.total in 1..2000) { "Backup sem registros ou acima do limite" }
        val entries = stage.evaluations.map { Triple("evaluation", it.id, it.toJson()) } +
            stage.charges.map { Triple("charge", it.id, it.toJson()) }
        val db = writableDatabase
        db.beginTransaction()
        try {
            for ((kind, id, _) in entries) {
                db.rawQuery("SELECT 1 FROM records WHERE kind=? AND id=?", arrayOf(kind,id)).use { cursor ->
                    require(!cursor.moveToNext()) { "Já existe um item deste backup ($id). Nenhum registro foi alterado." }
                }
            }
            for ((kind,id,record) in entries) {
                val values = ContentValues().apply {
                    put("kind",kind); put("id",id)
                    put("payload",encrypt(record.toString().toByteArray(Charsets.UTF_8)))
                }
                db.insertOrThrow("records",null,values)
            }
            db.setTransactionSuccessful()
        } finally { db.endTransaction() }
        return entries.size
    }

    /** Periodic same-device backup, without persisting a portable user's password.
     * Keystore protection cannot be restored if the app is uninstalled or Android is reset. */
    fun exportDeviceEncrypted(): ByteArray {
        val array = JSONArray()
        allowedKinds.forEach { kind -> list(kind).forEach { record ->
            array.put(JSONObject().put("kind",kind).put("record",record))
        } }
        val raw = JSONObject().put("schema",1).put("records",array).toString().toByteArray(Charsets.UTF_8)
        require(raw.size <= 16_000_000) { "Backup automático acima do limite" }
        return JSONObject().put("type","EF-DEVICE-BACKUP")
            .put("version",1).put("ciphertext",b64(encrypt(raw)))
            .toString().toByteArray(Charsets.UTF_8).also { raw.fill(0) }
    }
    fun importDeviceEncrypted(file: ByteArray): Int {
        require(file.size <= 24_000_000)
        val header = JSONObject(String(file, Charsets.UTF_8))
        require(header.getString("type") == "EF-DEVICE-BACKUP" && header.getInt("version") == 1)
        val plaintext = decrypt(from64(header.getString("ciphertext")))
        // Do not expose plaintext in external storage; wrap only in-memory for the same import machinery.
        val root = JSONObject(String(plaintext,Charsets.UTF_8)); plaintext.fill(0)
        val records = root.getJSONArray("records")
        require(root.getInt("schema") == 1 && records.length() <= 10000)
        val stage = mutableListOf<Triple<String,String,JSONObject>>()
        val seen = hashSetOf<String>()
        for (i in 0 until records.length()) {
            val entry=records.getJSONObject(i); val kind=entry.getString("kind")
            val record=entry.getJSONObject("record"); val id=record.getString("id")
            require(kind in allowedKinds && id.isNotBlank() && seen.add("$kind:$id"))
            stage.add(Triple(kind,id,record))
        }
        return importStaged(stage)
    }
    private fun importStaged(records: List<Triple<String,String,JSONObject>>): Int {
        // Full validation occurs before starting any write transaction.
        require(records.size <= 10000) { "Limite de itens ultrapassado" }
        val keys = HashSet<String>()
        records.forEach { (kind,id,record) ->
            require(kind in allowedKinds && id.isNotBlank() && id.length <= 128 &&
                id == record.getString("id") && keys.add("$kind:$id")) { "Registro inválido ou duplicado" }
            when(kind) {
                "evaluation" -> Evaluation.fromJson(record)
                "charge" -> Charge.fromJson(record)
                "intake" -> Intake.fromJson(record)
                "appointment" -> Appointment.fromJson(record)
                "task" -> TaskItem.fromJson(record).also(WorkspaceRules::validate)
                "training" -> WorkspaceRules.validate(TrainingSession.fromJson(record))
                "habit" -> WorkspaceRules.validate(HabitEntry.fromJson(record))
                "note" -> WorkspaceRules.validate(NotebookEntry.fromJson(record))
            }
        }
        val db=writableDatabase
        db.beginTransaction()
        try {
            // A backup can be restored more than once. Identical rows are skipped, while
            // conflicting current data are never silently overwritten or partially imported.
            val missing = mutableListOf<Triple<String,String,JSONObject>>()
            records.forEach { (kind,id,record) ->
                db.rawQuery("SELECT payload FROM records WHERE kind=? AND id=?", arrayOf(kind,id)).use { cursor ->
                    if (!cursor.moveToNext()) missing.add(Triple(kind,id,record))
                    else {
                        val original = String(decrypt(cursor.getBlob(0)), Charsets.UTF_8)
                        require(original == record.toString()) {
                            "Há uma versão diferente do registro $kind/$id. Restauração interrompida sem alterar dados."
                        }
                    }
                }
            }
            missing.forEach { (kind,id,record) ->
                val values=ContentValues().apply { put("kind",kind);put("id",id)
                    put("payload",encrypt(record.toString().toByteArray(Charsets.UTF_8))) }
                db.insertOrThrow("records",null,values)
            }
            db.setTransactionSuccessful()
            return missing.size
        } finally { db.endTransaction() }
    }

    private fun derive(passphrase: CharArray, salt: ByteArray): SecretKeySpec {
        val spec = PBEKeySpec(passphrase, salt, 210000, 256)
        try { return SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded, "AES") }
        finally { spec.clearPassword() }
    }
    private fun b64(bytes: ByteArray) = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun from64(s: String) = Base64.decode(s, Base64.NO_WRAP)
}
