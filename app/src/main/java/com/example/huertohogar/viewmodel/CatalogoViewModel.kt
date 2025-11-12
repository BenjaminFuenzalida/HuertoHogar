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

/**
 * Estado de la UI para la pantalla del Catálogo.
 * Contiene la lista de productos y el estado del carrito.
 */
data class CatalogoUiState(
    val productos: List<Producto> = emptyList(),
    // Usamos un Mapa para el carrito: Key = ID del Producto, Value = Cantidad
    val carrito: Map<Int, Int> = emptyMap(),
    val cargando: Boolean = true,
    val error: String? = null
)

class CatalogoViewModel(private val productoDao: ProductoDao) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        cargarProductos()
    }

    /**
     * Carga los productos desde la base de datos (Room)
     */
    private fun cargarProductos() {
        viewModelScope.launch {
            productoDao.obtenerTodosLosProductos()
                .catch { e ->
                    // Manejar error de la base de datos
                    _uiState.update { it.copy(error = "Error al cargar productos: ${e.message}", cargando = false) }
                }
                .collect { productos ->
                    // Productos cargados exitosamente
                    _uiState.update { it.copy(productos = productos, cargando = false) }
                }
        }
    }

    /**
     * Agrega un producto al carrito de compras.
     */
    fun agregarAlCarrito(producto: Producto) {
        _uiState.update { estadoActual ->
            val carritoActual = estadoActual.carrito.toMutableMap()

            // Aumenta la cantidad del producto en el carrito
            val cantidadActual = carritoActual.getOrDefault(producto.id, 0)
            carritoActual[producto.id] = cantidadActual + 1

            estadoActual.copy(carrito = carritoActual)
        }
    }

    /**
     * Elimina un producto del carrito.
     */
    fun eliminarDelCarrito(producto: Producto) {
        _uiState.update { estadoActual ->
            val carritoActual = estadoActual.carrito.toMutableMap()

            val cantidadActual = carritoActual.getOrDefault(producto.id, 0)
            if (cantidadActual > 1) {
                // Si hay más de 1, reduce la cantidad
                carritoActual[producto.id] = cantidadActual - 1
            } else {
                // Si hay 1 o 0, elimina el producto del carrito
                carritoActual.remove(producto.id)
            }

            estadoActual.copy(carrito = carritoActual)
        }
    }

    /**
     * Simula la comprobación de inicio de sesión.
     */
    fun usuarioEstaLogueado(): Boolean {
        // Por ahora, simulamos que el usuario NUNCA está logueado
        // para forzar la lógica de redirección al login.
        return false
    }
}
