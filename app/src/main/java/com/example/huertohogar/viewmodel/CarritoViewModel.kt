package com.example.huertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.datos.CarritoDao
import com.example.huertohogar.modelos.CarritoItem
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.modelos.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CarritoUiState(
    val items: List<CarritoItem> = emptyList(),
    val productos: List<Producto> = emptyList()
)

class CarritoViewModel(private val carritoDao: CarritoDao) : ViewModel() {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    fun cargarCarrito(usuario: Usuario) {
        viewModelScope.launch {
            val items = carritoDao.getCarritoPorUsuario(usuario.id)
            _uiState.update { it.copy(items = items) }
        }
    }

    fun agregarAlCarrito(usuario: Usuario, producto: Producto) {
        viewModelScope.launch {
            val itemExistente = _uiState.value.items.find { it.productoId == producto.id }
            val nuevaCantidad = (itemExistente?.cantidad ?: 0) + 1
            val nuevoItem = CarritoItem(usuario.id, producto.id, nuevaCantidad)
            carritoDao.agregarOActualizarItem(nuevoItem)
            cargarCarrito(usuario)
        }
    }

    fun eliminarDelCarrito(usuario: Usuario, producto: Producto) {
        viewModelScope.launch {
            val itemExistente = _uiState.value.items.find { it.productoId == producto.id }
            if (itemExistente != null) {
                if (itemExistente.cantidad > 1) {
                    val nuevaCantidad = itemExistente.cantidad - 1
                    val nuevoItem = CarritoItem(usuario.id, producto.id, nuevaCantidad)
                    carritoDao.agregarOActualizarItem(nuevoItem)
                } else {
                    carritoDao.eliminarItem(usuario.id, producto.id)
                }
                cargarCarrito(usuario)
            }
        }
    }

    fun limpiarCarrito(usuario: Usuario) {
        viewModelScope.launch {
            carritoDao.limpiarCarrito(usuario.id)
            _uiState.update { it.copy(items = emptyList()) }
        }
    }
}