package br.com.evolufisico.nativo.data

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest
import java.text.Normalizer
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** Import ONLY explicit user-selected exports, never read private data from other installed apps.
 * Recognizes the beta 7.1.2 backup envelope, old arrays and password-protected .efx files.
 * Import is additive/atomic; original JSON is retained in extendedAnswers["_legacy_json"].
 */
object LegacyImporter {
    data class Stage(val evaluations: List<Evaluation>, val charges: List<Charge>,
                     val hadChecksum: Boolean, val version: String) {
        val total: Int get() = evaluations.size + charges.size
    }
    fun parse(file: ByteArray, password: CharArray? = null): Stage {
        require(file.size in 2..24_000_000) { "Backup vazio ou maior que 24 MB" }
        val text = String(file, Charsets.UTF_8).trimStart('\uFEFF', ' ', '\n', '\r', '\t')
        val source = if (text.startsWith("{")) JSONObject(text) else null
        val json = if (source?.optBoolean("encrypted") == true) {
            require(source.optString("security") == "AES-256-GCM") { "Proteção desconhecida" }
            val kdf = source.optJSONObject("kdf") ?: error("Backup antigo não possui KDF portátil; exporte novamente no aplicativo original")
            val secret = password ?: error("Informe a senha mestra desse backup .efx")
            val salt = Base64.decode(kdf.getString("salt"), Base64.DEFAULT)
            val iters = kdf.optInt("iterations")
            require(salt.size == 16 && iters in 100_000..1_000_000) { "Parâmetros de chave inválidos" }
            val keySpec = PBEKeySpec(secret, salt, iters, 256)
            val key = try { SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(keySpec).encoded }
                finally { keySpec.clearPassword() }
            val packet = source.getJSONObject("payload")
            require(packet.getString("alg") == "AES-256-GCM")
            val iv = Base64.decode(packet.getString("iv"), Base64.DEFAULT)
            require(iv.size == 12) { "Vetor criptográfico inválido" }
            val raw = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
            }.doFinal(Base64.decode(packet.getString("ct"), Base64.DEFAULT))
            key.fill(0)
            val decrypted = String(raw, Charsets.UTF_8)
            raw.fill(0)
            decrypted
        } else text
        return readJson(json)
    }
    private fun readJson(text: String): Stage {
        val root = if (text.trimStart().startsWith("[")) null else JSONObject(text)
        val list = if (root == null) JSONArray(text) else when {
            root.has("avaliacoes") -> root.getJSONArray("avaliacoes")
            root.has("data") -> root.getJSONArray("data")
            root.has("evaluations") -> root.getJSONArray("evaluations")
            root.has("nome") -> JSONArray().put(root)
            else -> error("Formato legado não reconhecido")
        }
        require(list.length() <= 1_000) { "Máximo de mil avaliações por backup" }
        val checksum = root?.optString("checksum").orEmpty()
        if (checksum.isNotBlank()) {
            // Original export calculates SHA-256(JSON.stringify(avaliacoes)) before pretty-printing.
            val actual = hash(list.toString().toByteArray(Charsets.UTF_8))
            require(actual.equals(checksum, ignoreCase = true)) {
                "Checksum divergente. Não foram importados dados; exporte novamente o backup original."
            }
        }
        val financial = root?.optJSONArray("financeiro")
        if (financial != null) {
            val check = root?.optString("checksumFinanceiro").orEmpty()
            require(check.isNotBlank() && hash(financial.toString().toByteArray(Charsets.UTF_8)).equals(check, true)) {
                "Checksum financeiro incompatível" }
        }
        val ids = HashSet<String>()
        val students = HashMap<String, String>()
        val evaluations = (0 until list.length()).map { i ->
            val record = list.getJSONObject(i)
            val origin = record.optString("id").takeIf { it.isNotBlank() } ?: "index-$i"
            val key = "legacy-$origin"
            require(ids.add("evaluation:$key")) { "ID duplicado no backup" }
            val name = record.optString("nome", record.optString("name")).ifBlank { "Aluno sem nome" }
            val email = record.optString("email").trim().lowercase()
            val phone = record.optString("whatsapp", record.optString("telefone")).filter(Char::isDigit)
            // The original student ID takes precedence. Two namesakes must never be joined.
            val originalStudentId = record.optString("cliente_id",record.optString("studentId")).trim()
            val identity = when {
                originalStudentId.isNotBlank() -> "id:$originalStudentId"
                email.isNotEmpty() -> "email:$email"
                phone.isNotEmpty() -> "phone:$phone"
                else -> "name:${Normalizar.forIndex(name)}"
            }
            val studentId = students.getOrPut(identity) { UUID.nameUUIDFromBytes("legacy:$identity".toByteArray()).toString() }
            fun num(field: String): Double? = record.optionalDouble(field)
            fun map(obj: JSONObject?): Map<String, Double> = obj?.keys()?.asSequence()
                ?.mapNotNull { k -> obj?.optionalDouble(k)?.let { k to it } }?.toMap() ?: emptyMap()
            fun strMap(obj: JSONObject?): Map<String,String> = obj?.keys()?.asSequence()
                ?.map { it to obj?.optString(it).orEmpty() }?.toMap() ?: emptyMap()
            val sex = when(record.optString("sexo").lowercase()) { "m", "masculino" -> "Masculino"
                "f", "feminino" -> "Feminino"; else -> "Não informado" }
            val originalDate = record.optString("data_avaliacao",record.optString("date"))
            val date = normalizeDate(originalDate)
                ?: if (originalDate.isBlank()) LocalDate.now().toString() else
                    error("Data de avaliação desconhecida: preserve o backup original e corrija a data antes da importação")
            val perception = record.optString("aderencia_treino")
            val diet = record.optString("aderencia_dieta")
            val neverTraining = perception.contains("nunca", true) || perception.contains("não se aplica", true)
            val neverDiet = diet.contains("nunca", true) || diet.contains("não se aplica", true)
            val trained = perception.isNotBlank() && !neverTraining
            val dieted = diet.isNotBlank() && !neverDiet
            val pics = strMap(record.optJSONObject("fotos")).mapValues { (_, v) ->
                // Retain legacy SVG/PNG/JPEG data URIs for evidence; the native viewer only decodes bitmap files.
                if (v.startsWith("data:image/jpeg;base64,", true)) v.substringAfter(',') else v
            }
            val sourcePerimeters = record.optJSONObject("perimetria")
            val perimeters = map(sourcePerimeters).toMutableMap()
            for (key in listOf("pescoco", "ombros", "abdomen", "cintura", "quadril", "braco_dir",
                "braco_esq", "coxa_dir", "coxa_esq", "panturrilha")) {
                if (key !in perimeters) (record.optionalDouble(key) ?: record.optionalDouble("peri_$key"))?.let { perimeters[key] = it }
            }
            val skinfolds = map(record.optJSONObject("dobras")).toMutableMap()
            for (key in listOf("triceps", "subescapular", "peitoral", "axilar", "suprailiaca", "abdominal", "coxa")) {
                if (key !in skinfolds) record.optionalDouble("dobra_$key")?.let { skinfolds[key] = it }
            }
            val bioimpedance = map(record.optJSONObject("bioimpedancia")).toMutableMap()
            for (key in listOf("peso", "pgordura", "pmusculo", "imc", "gordura_kg", "mme", "tmb", "visceral", "idade_corporal", "rcq")) {
                if (key !in bioimpedance) record.optionalDouble("bia_$key")?.let { bioimpedance[key] = it }
            }
            Evaluation(id=key, studentId=studentId, name=name, sex=sex, age=record.optionalInt("idade"),
                date=date, weightKg=num("peso"), heightCm=num("altura"),
                waistCm=num("cintura") ?: perimeters["cintura"],
                hipCm=num("quadril") ?: perimeters["quadril"],
                bodyFatPercent=bioimpedance["pgordura"],
                trainingExperienced=trained, dietExperienced=dieted,
                trainingAdherence=if (trained) legacyAdherence(perception,false) else null,
                dietAdherence=if (dieted) legacyAdherence(diet,true) else null,
                notes=record.optString("parecer_profissional", record.optString("observacoes")),
                email=email, phone=phone, nextAssessment=record.optString("data_proxima_avaliacao"),
                phase=record.optString("fase"), week=record.optString("checkin_semana"),
                energy=record.optionalInt("energia"), professional=record.optString("avaliador_nome"),
                registration=record.optString("avaliador_registro"), clinic=record.optString("avaliador_clinica"),
                perimeters=perimeters,
                skinfolds=skinfolds,
                bioimpedance=bioimpedance, photos=pics,
                extendedAnswers=buildMap {
                    put("_legacy_json",record.toString())
                    val previous=record.optJSONObject("parecer_v6")
                    if(previous!=null) {
                        put("_legacy_assessment",previous.toString())
                        mapOf("status" to "parecer_status", "tags" to "parecer_tags",
                            "sono" to "parecer_sono", "estresse" to "parecer_estresse",
                            "dor" to "parecer_dor", "meta_peso" to "meta_peso",
                            "meta_pgordura" to "meta_pgordura", "meta_prazo" to "meta_prazo",
                            "pontos_fortes" to "pontos_fortes", "pontos_atencao" to "pontos_atencao",
                            "ajuste_treino" to "ajuste_treino", "ajuste_dieta" to "ajuste_dieta",
                            "proximos_passos" to "proximos_passos")
                            .forEach { (old,new)-> if(previous.has(old)&&!previous.isNull(old)) put(new,previous.optString(old)) }
                    }
                    val bia=record.optJSONObject("bioimpedancia")
                    if(bia!=null) for(key in listOf("risco", "analise_texto"))
                        if(bia.has(key)) put("bia_$key",bia.optString(key))
                })
        }
        val charges = (0 until (financial?.length() ?: 0)).map { i ->
            val r = financial!!.getJSONObject(i)
            val id = "legacy-${r.optString("id", "charge-$i")}"; require(ids.add("charge:$id"))
            val student = r.optJSONObject("aluno1")?.optString("nome").orEmpty()
            val status = r.optString("status",r.optString("statusPagamento", "pendente")).lowercase()
            val paid = status == "pago"
            val exempt = status in setOf("cortesia", "isento", "isenção")
            val net = currencyCents(r.optionalDouble("valorTotal") ?: 0.0)
            val gross = r.optionalDouble("subtotal")?.let(::currencyCents)?.coerceAtLeast(net) ?: net
            val originalDue = r.optString("data")
            val due = normalizeDate(originalDue)
                ?: if (originalDue.isBlank()) LocalDate.now().toString() else error("Data inválida na cobrança legada")
            Charge(id=id,student=student,secondStudent=r.optJSONObject("aluno2")?.optString("nome").orEmpty(),
                service=r.optString("servicoNome", r.optString("servicoId", "Serviço legado")),
                amountCents=gross,dueDate=due,paid=paid,
                paymentMethod=r.optString("formaPagamento","Pix"),
                installments=r.optInt("parcelasCartao",1).coerceIn(1,24),
                discountCents=gross-net,exempt=exempt,notes="Importado de backup legado | Original: ${r}")
        }
        return Stage(evaluations, charges, checksum.isNotBlank(), root?.optString("versaoApp","Legado") ?: "Legado")
    }
    private fun currencyCents(value: Double): Long {
        require(value.isFinite() && value in 0.0..1_000_000_000.0) { "Valor financeiro inválido no backup" }
        return BigDecimal.valueOf(value).multiply(BigDecimal(100))
            .setScale(0,RoundingMode.HALF_UP).longValueExact()
    }
    private fun normalizeDate(value: String): String? {
        if (value.isBlank()) return null
        val clean = value.trim()
        return runCatching { LocalDate.parse(clean).toString() }.getOrNull()
            ?: runCatching { LocalDate.parse(clean.take(10)).toString() }.getOrNull()
            ?: runCatching { LocalDate.parse(clean,DateTimeFormatter.ofPattern("dd/MM/uuuu")).toString() }.getOrNull()
    }
    private fun legacyAdherence(text:String,diet:Boolean): Int? {
        val label=text.trim().lowercase()
        val scores=if(diet) mapOf("100% no plano" to 100, "95% no plano" to 95,
            "pequenos furos" to 75, "muitos furos" to 40, "fora do plano" to 10)
        else mapOf("100% no plano" to 100, "faltei 1-2x" to 75,
            "faltei 3x ou mais" to 40, "não treinei" to 0)
        return scores[label] ?: Regex("^\\d{1,3}(?:[.,]\\d+)?%$").matchEntire(label)?.value
            ?.dropLast(1)?.replace(',','.')?.toDoubleOrNull()?.toInt()?.takeIf { it in 0..100 }
    }
    private fun hash(b: ByteArray) = MessageDigest.getInstance("SHA-256").digest(b)
        .joinToString("") { "%02x".format(it) }
}
private object Normalizar {
    fun forIndex(s: String) = Normalizer.normalize(s.trim().lowercase(),Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").replace(Regex("\\s+"), " ")
}
