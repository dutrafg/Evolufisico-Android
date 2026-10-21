package br.com.evolufisico.android.data

import java.util.UUID

/** ID permanente: contatos opcionais não alteram vínculos com avaliações e cobranças. */
data class Client(
    val id:String,
    val name:String,
    val phone:String="",
    val email:String="",
    val birthDateIso:String=""
) {
    companion object {
        fun new(name:String,phone:String="",email:String="",birthDateIso:String="") =
            Client(UUID.randomUUID().toString(),name.trim(),
                phone.trim(),email.trim(),birthDateIso.trim())
    }
}
