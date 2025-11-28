package com.example.huertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.datos.UsuarioDao
import com.example.huertohogar.modelos.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistroUiState(
    val registroExitoso: Boolean = false,
    val error: String? = null
)

class RegistroViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    init {
        crearAdminPorDefecto()
    }

    private fun crearAdminPorDefecto() {
        viewModelScope.launch {
            val admin = usuarioDao.buscarUsuarioPorEmail("admin@huertohogar.com")
            if (admin == null) {
                val adminUsuario = Usuario(
                    email = "admin@huertohogar.com",
                    contrasena = "admin123",
                    isAdmin = true
                )
                usuarioDao.registrarUsuario(adminUsuario)
            }
        }
    }

    fun registrarUsuario(email: String, contrasena: String, repetirContrasena: String) {
        if (email.isBlank() || contrasena.isBlank() || repetirContrasena.isBlank()) {
            _uiState.update { it.copy(error = "Todos los campos son obligatorios") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(error = "El correo no es válido") }
            return
        }
        if (contrasena != repetirContrasena) {
            _uiState.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }
        if (contrasena.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        viewModelScope.launch {
            try {
                val usuarioExistente = usuarioDao.buscarUsuarioPorEmail(email)
                if (usuarioExistente != null) {
                    _uiState.update { it.copy(error = "El correo ya está registrado") }
                    return@launch
                }

                val nuevoUsuario = Usuario(email = email, contrasena = contrasena)
                usuarioDao.registrarUsuario(nuevoUsuario)

                _uiState.update { it.copy(registroExitoso = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al registrar: ${e.message}") }
            }
        }
    }
}