package com.example.huertohogar.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.modelos.Usuario

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
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}
