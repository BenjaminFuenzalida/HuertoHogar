package com.example.huertohogar.modelos

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "carrito_items",
    primaryKeys = ["usuarioId", "productoId"],
    indices = [Index("productoId")],
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Producto::class,
            parentColumns = ["id"],
            childColumns = ["productoId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class CarritoItem(
    val usuarioId: Int,
    val productoId: Int,
    val cantidad: Int
)
