package com.example.huertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.datos.ProductoDao
import com.example.huertohogar.modelos.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val productos: List<Producto> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null,
    val productoSeleccionado: Producto? = null
)

class AdminViewModel(private val productoDao: ProductoDao) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    private fun cargarProductos() {
        viewModelScope.launch {
            productoDao.obtenerTodosLosProductos()
                .catch { e ->
                    _uiState.update { it.copy(error = "Error al cargar productos: ${e.message}", cargando = false) }
                }
                .collect { productos ->
                    _uiState.update { it.copy(productos = productos, cargando = false) }
                }
        }
    }

    fun getProducto(id: Int) {
        viewModelScope.launch {
            val producto = productoDao.getProductoPorId(id)
            _uiState.update { it.copy(productoSeleccionado = producto) }
        }
    }

    fun guardarProducto(producto: Producto) {
        viewModelScope.launch {
            if (producto.id == 0) {
                productoDao.insertarProducto(producto)
            } else {
                productoDao.actualizarProducto(producto)
            }
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            productoDao.eliminarProducto(id)
        }
    }

    fun limpiarProductoSeleccionado() {
        _uiState.update { it.copy(productoSeleccionado = null) }
    }
}