package com.example.huertohogar.datos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.huertohogar.modelos.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun registrarUsuario(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE email = :email LIMIT 1")
    suspend fun buscarUsuarioPorEmail(email: String): Usuario?
}
