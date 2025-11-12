package com.example.huertohogar.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.huertohogar.modelos.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun obtenerTodosLosProductos(): Flow<List<Producto>> // Flow es para datos reactivos

    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)

    // Podrías añadir datos de ejemplo al crear la BBDD
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarProductosIniciales(productos: List<Producto>)
}
