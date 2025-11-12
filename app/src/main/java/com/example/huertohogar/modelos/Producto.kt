package com.example.huertohogar.modelos

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Modelo de datos para un Producto (Almacenamiento Local)
 * @Entity le dice a Room que esto es una tabla.
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val stock: Int,
    val categoria: String,
    val origen: String, // Información extra
    val imagenUrl: String // Usamos URLs de placeholder por ahora
)

