package com.example.huertohogar.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.modelos.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// --- Definición de la Base de Datos (Almacenamiento Local) ---

@Database(entities = [Producto::class, Usuario::class], version = 1, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        // @Volatile asegura que la instancia sea siempre la más actual
        @Volatile
        private var INSTANCIA: BaseDeDatosApp? = null

        fun getInstancia(contexto: Context): BaseDeDatosApp {
            // synchronized previene que dos hilos de ejecución creen la BBDD al mismo tiempo
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDeDatosApp::class.java,
                    "huertohogar_db" // Nombre del archivo de la base de datos
                )
                    .fallbackToDestructiveMigration() // Si cambiamos la versión, borra y recrea la BBDD
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Insertar datos iniciales en un hilo separado
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCIA?.let {
                                    poblarBaseDeDatos(it.productoDao())
                                }
                            }
                        }
                    })
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }

        suspend fun poblarBaseDeDatos(productoDao: ProductoDao) {
            val productosIniciales = listOf(
                Producto(nombre = "Manzana Fuji", precio = 1500.0, imagenUrl = "https://images.unsplash.com/photo-1568702846914-96b305d2aaeb?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80"),
                Producto(nombre = "Plátano", precio = 990.0, imagenUrl = "https://images.unsplash.com/photo-1571771894824-c63b5f47b93a?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2848&q=80"),
                Producto(nombre = "Lechuga Costina", precio = 800.0, imagenUrl = "https://images.unsplash.com/photo-1557844039-b91d29311d75?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80"),
                Producto(nombre = "Tomate Larga Vida", precio = 1200.0, imagenUrl = "https://images.unsplash.com/photo-1582284540020-8acbe03f4924?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80"),
                Producto(nombre = "Zanahoria", precio = 700.0, imagenUrl = "https://images.unsplash.com/photo-1590413323594-43c32049d5a6?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80"),
                Producto(nombre = "Pimentón Rojo", precio = 1100.0, imagenUrl = "https://images.unsplash.com/photo-1599292390066-c95689139ea9?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2940&q=80")
            )
            productoDao.insertarProductosIniciales(productosIniciales)
        }
    }
}
