package br.com.evolufisico.android.data

import java.util.UUID

data class Client(val id: String, val name: String) {
    companion object {
        fun new(name: String): Client = Client(UUID.randomUUID().toString(), name.trim())
    }
}
