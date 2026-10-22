package br.com.evolufisico.nativo.data

import org.json.JSONObject
import kotlin.math.floor

/** Reject malformed optional measurements before model parsers can interpret them as absent. */
object RestoreJsonRules {
    fun validate(kind:String,record:JSONObject) {
        fun number(obj:JSONObject,key:String,integer:Boolean=false) {
            if(!obj.has(key) || obj.isNull(key))return
            val value=obj.getDouble(key)
            require(value.isFinite() && (!integer || (value==floor(value) && value in Int.MIN_VALUE.toDouble()..Int.MAX_VALUE.toDouble()))) {
                "Campo numérico inválido no backup: $kind/$key"
            }
        }
        when(kind) {
            "evaluation" -> {
                listOf("weightKg","heightCm","waistCm","hipCm","bodyFatPercent","goalWeightKg","goalWeightBaselineKg",
                    "goalBodyFatPercent","goalBodyFatBaselinePercent").forEach{number(record,it)}
                listOf("age","energy","trainingAdherence","dietAdherence").forEach{number(record,it,true)}
                for(key in listOf("perimeters","skinfolds","bioimpedance")) {
                    if(!record.has(key) || record.isNull(key))continue
                    val values=record.optJSONObject(key) ?: error("Mapa de medidas inválido: $key")
                    values.keys().forEach{field->
                        require(!values.isNull(field)) { "Medida nula em $key/$field" }
                        number(values,field)
                    }
                }
            }
            "charge" -> {
                number(record,"installments",true)
                for(key in listOf("amountCents","discountCents")) {
                    if(!record.has(key) || record.isNull(key))continue
                    val value=record.getDouble(key)
                    require(value.isFinite() && value==floor(value) && value in 0.0..1_000_000_000_000.0) {
                        "Valor em centavos inválido: $key"
                    }
                }
            }
            "appointment" -> number(record,"durationMinutes",true)
            "task" -> number(record,"priority",true)
            "training" -> {
                number(record,"durationMinutes",true);number(record,"effort",true)
                if(record.has("sets")) {
                    val sets=record.getJSONArray("sets")
                    require(sets.length()<=200) { "Treino acima de 200 séries." }
                    for(index in 0 until sets.length()) {
                        val row=sets.getJSONObject(index)
                        number(row,"repetitions",true);number(row,"loadKg")
                    }
                }
            }
        }
    }
}
