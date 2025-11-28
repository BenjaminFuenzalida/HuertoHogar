package com.example.huertohogar.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.huertohogar.modelos.CarritoItem

@Dao
interface CarritoDao {

    @Query("SELECT * FROM carrito_items WHERE usuarioId = :usuarioId")
    suspend fun getCarritoPorUsuario(usuarioId: Int): List<CarritoItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarOActualizarItem(item: CarritoItem)

    @Query("DELETE FROM carrito_items WHERE usuarioId = :usuarioId AND productoId = :productoId")
    suspend fun eliminarItem(usuarioId: Int, productoId: Int)

    @Query("DELETE FROM carrito_items WHERE usuarioId = :usuarioId")
    suspend fun limpiarCarrito(usuarioId: Int)
}
