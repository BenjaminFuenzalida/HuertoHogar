package com.example.huertohogar.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.huertohogar.modelos.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Query("SELECT * FROM productos")
    fun obtenerTodosLosProductos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun getProductoPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE categoria = :categoria")
    fun obtenerProductosPorCategoria(categoria: String): Flow<List<Producto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarProducto(producto: Producto)

    @Update
    suspend fun actualizarProducto(producto: Producto)

    @Query("DELETE FROM productos WHERE id = :id")
    suspend fun eliminarProducto(id: Int)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertarProductosIniciales(productos: List<Producto>)
}
