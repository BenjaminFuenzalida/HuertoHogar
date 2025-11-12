package com.huertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.datos.UsuarioDao
import com.example.huertohogar.modelos.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// --- 1. Definir el ESTADO de la UI ---
// Esta data class representa todo lo que se muestra en la pantalla
data class RegistroUiState(
    val email: String = "",
    val contrasena: String = "",
    val repetirContrasena: String = "",
    val mensajeError: String? = null,
    val registroExitoso: Boolean = false
)

// --- 2. Crear el ViewModel ---
// Necesita el UsuarioDao para interactuar con la base de datos
class RegistroViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    // _uiState es privado y mutable, solo el ViewModel puede cambiarlo
    private val _uiState = MutableStateFlow(RegistroUiState())

    // uiState es público e inmutable, la UI solo puede leerlo
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    // --- 3. Funciones para actualizar el estado desde la UI ---

    fun onEmailChange(email: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(email = email, mensajeError = null)
        }
    }

    fun onContrasenaChange(contrasena: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(contrasena = contrasena, mensajeError = null)
        }
    }

    fun onRepetirContrasenaChange(contrasena: String) {
        _uiState.update { estadoActual ->
            estadoActual.copy(repetirContrasena = contrasena, mensajeError = null)
        }
    }

    // --- 4. Lógica de Negocio  ---

    fun registrarUsuario() {
        val estado = _uiState.value // Obtiene el estado actual

        // --- Validación de Formulario (en el ViewModel) ---
        if (estado.email.isBlank() || estado.contrasena.isBlank() || estado.repetirContrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Todos los campos son obligatorios") }
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(estado.email).matches()) {
            _uiState.update { it.copy(mensajeError = "El correo no es válido") }
            return
        }
        if (estado.contrasena != estado.repetirContrasena) {
            _uiState.update { it.copy(mensajeError = "Las contraseñas no coinciden") }
            return
        }
        if (estado.contrasena.length < 6) {
            _uiState.update { it.copy(mensajeError = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        // --- 5. Ejecutar la tarea pesada en un hilo de fondo ---
        viewModelScope.launch {
            try {
                // Comprobar si el usuario ya existe
                val usuarioExistente = usuarioDao.buscarUsuarioPorEmail(estado.email)
                if (usuarioExistente != null) {
                    _uiState.update { it.copy(mensajeError = "El correo ya está registrado") }
                    return@launch // Termina la corrutina
                }

                // Crear y guardar el nuevo usuario
                val nuevoUsuario = Usuario(
                    email = estado.email,
                    contrasena = estado.contrasena
                )
                usuarioDao.registrarUsuario(nuevoUsuario)

                // Actualizar el estado para notificar a la UI que fue exitoso
                _uiState.update { it.copy(registroExitoso = true) }

            } catch (e: Exception) {
                // Manejar cualquier error de la base de datos
                _uiState.update { it.copy(mensajeError = "Error al registrar: ${e.message}") }
            }
        }
    }
}
