package com.example.huertohogar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huertohogar.datos.ProductoDao
import com.example.huertohogar.modelos.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Estado de la UI para la pantalla del Catálogo.
 */
data class CatalogoUiState(
    val productos: List<Producto> = emptyList(),
    val cargando: Boolean = true,
    val error: String? = null
)

class CatalogoViewModel(private val productoDao: ProductoDao) : ViewModel() {

    private val _uiState = MutableStateFlow(CatalogoUiState())
    val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

    init {
        cargarOInicializarProductos()
    }

    /**
     * Carga los productos y, si la base de datos está vacía, la puebla con datos iniciales.
     */
    private fun cargarOInicializarProductos() {
        viewModelScope.launch {
            val productosActuales = productoDao.obtenerTodosLosProductos().first()

            if (productosActuales.isEmpty()) {
                val productosIniciales = listOf(
                    Producto(nombre = "Manzana Fuji", descripcion = "Manzana roja y dulce", precio = 1500.0, stock = 100, categoria = "Fruta", origen = "Chile", imagenUrl = "https://plus.unsplash.com/premium_photo-1673544920963-840455e34582"),
                    Producto(nombre = "Plátano", descripcion = "Plátano maduro y listo para comer", precio = 990.0, stock = 150, categoria = "Fruta", origen = "Ecuador", imagenUrl = "https://images.pexels.com/photos/1093038/pexels-photo-1093038.jpeg"),
                    Producto(nombre = "Lechuga Costina", descripcion = "Lechuga fresca y crujiente", precio = 800.0, stock = 200, categoria = "Verdura", origen = "Chile", imagenUrl = "https://images.unsplash.com/photo-1557844039-b91d29311d75"),
                    Producto(nombre = "Tomate Larga Vida", descripcion = "Tomate ideal para ensaladas", precio = 1200.0, stock = 120, categoria = "Verdura", origen = "Chile", imagenUrl = "https://images.unsplash.com/photo-1582284540020-8acbe03f4924"),
                    Producto(nombre = "Zanahoria", descripcion = "Zanahoria fresca y llena de vitaminas", precio = 700.0, stock = 180, categoria = "Verdura", origen = "Chile", imagenUrl = "https://images.pexels.com/photos/1306559/pexels-photo-1306559.jpeg"),
                    Producto(nombre = "Pimentón Rojo", descripcion = "Pimentón rojo para cocinar", precio = 1100.0, stock = 90, categoria = "Verdura", origen = "Chile", imagenUrl = "https://images.pexels.com/photos/161556/tomatoes-vegetables-red-fresh-161556.jpeg")
                )
                productoDao.insertarProductosIniciales(productosIniciales)
            }

            productoDao.obtenerTodosLosProductos()
                .catch { e ->
                    _uiState.update { it.copy(error = "Error al cargar productos: ${e.message}", cargando = false) }
                }
                .collect { productos ->
                    _uiState.update { it.copy(productos = productos, cargando = false) }
                }
        }
    }
}
