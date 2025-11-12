package com.example.huertohogar.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos para un Usuario.
 */
@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email: String,
    val contrasena: String,
    val direccion: String? = null,
    val numeroContacto: String? = null
)
