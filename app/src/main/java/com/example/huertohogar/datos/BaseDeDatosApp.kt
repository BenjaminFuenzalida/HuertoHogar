package com.example.huertohogar.datos

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.huertohogar.modelos.CarritoItem
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.modelos.Usuario

@Database(entities = [Producto::class, Usuario::class, CarritoItem::class], version = 3, exportSchema = false)
abstract class BaseDeDatosApp : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun carritoDao(): CarritoDao

    companion object {
        @Volatile
        private var INSTANCIA: BaseDeDatosApp? = null

        fun getInstancia(contexto: Context): BaseDeDatosApp {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    BaseDeDatosApp::class.java,
                    "huertohogar_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}