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

/**
 * Estado de la UI para la pantalla de Login.
 */
data class LoginUiState(
    val usuario: Usuario? = null,
    val error: String? = null,
    val loginExitoso: Boolean = false,
    val isUserLoggedIn: Boolean = false
)

/**
 * ViewModel para la pantalla de Login.
 */
class LoginViewModel(
    private val usuarioDao: UsuarioDao,
    private val carritoViewModel: CarritoViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    /**
     * Inicia sesión con el correo y la contraseña proporcionados.
     */
    fun login(email: String, contrasena: String) {
        viewModelScope.launch {
            val usuario = usuarioDao.buscarUsuarioPorEmail(email)

            if (usuario != null && usuario.contrasena == contrasena) {
                _uiState.update { it.copy(usuario = usuario, loginExitoso = true, isUserLoggedIn = true) }
                carritoViewModel.cargarCarrito(usuario)
            } else {
                _uiState.update { it.copy(error = "Correo o contraseña incorrectos", isUserLoggedIn = false) }
            }
        }
    }

    /**
     * Cierra la sesión del usuario.
     */
    fun logout() {
        _uiState.value.usuario?.let { carritoViewModel.limpiarCarrito(it) }
        _uiState.update { it.copy(usuario = null, loginExitoso = false, isUserLoggedIn = false) }
    }
}