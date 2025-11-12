package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.huertohogar.datos.BaseDeDatosApp
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme
import com.example.huertohogar.viewmodel.CatalogoViewModel
import java.text.NumberFormat
import java.util.Locale

/**
 * Pantalla del Carrito de Compras.
 * Muestra los productos, cantidades y el total.
 * Implementa la lógica de "Finalizar Compra".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito(navController: NavController) {

    // --- 1. Obtener el ViewModel ---
    // Obtenemos la misma instancia que usó PantallaCatalogo
    val contexto = LocalContext.current.applicationContext
    val productoDao = remember { BaseDeDatosApp.getInstancia(contexto).productoDao() }

    val factory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(CatalogoViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return CatalogoViewModel(productoDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
    val viewModel: CatalogoViewModel = viewModel(factory = factory)

    // --- 2. Observar el Estado ---
    val uiState by viewModel.uiState.collectAsState()

    // Filtra la lista completa de productos para obtener solo los que están en el carrito
    val productosEnCarrito = remember(uiState.productos, uiState.carrito) {
        uiState.productos.filter { uiState.carrito.containsKey(it.id) }
    }

    // Calcula el total
    val totalCarrito = remember(productosEnCarrito, uiState.carrito) {
        productosEnCarrito.sumOf { producto ->
            val cantidad = uiState.carrito[producto.id] ?: 0
            producto.precio * cantidad
        }
    }

    // Formateador de moneda
    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    formatoMoneda.maximumFractionDigits = 0 // Sin decimales

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
            // Barra inferior con el total y botón de pago
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.height(100.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Total:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            formatoMoneda.format(totalCarrito),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Button(
                        onClick = {

                            if (viewModel.usuarioEstaLogueado()) {
                                // El usuario SÍ está logueado, ir a pantalla de pago (no creada)
                                // navController.navigate(Rutas.Pago.ruta)
                            } else {
                                // El usuario NO está logueado, redirigir a Login
                                navController.navigate(Rutas.Login.ruta)
                            }
                        },
                        modifier = Modifier.height(50.dp)
                    ) {
                        Text("Finalizar Compra")
                    }
                }
            }
        }
    ) { paddingValues ->

        if (productosEnCarrito.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(productosEnCarrito) { producto ->
                    val cantidad = uiState.carrito[producto.id] ?: 0
                    ItemCarrito(
                        producto = producto,
                        cantidad = cantidad,
                        onAgregar = { viewModel.agregarAlCarrito(producto) },
                        onRestar = { viewModel.eliminarDelCarrito(producto) },
                        formatoMoneda = formatoMoneda
                    )
                }
            }
        }
    }
}

@Composable
fun ItemCarrito(
    producto: Producto,
    cantidad: Int,
    onAgregar: () -> Unit,
    onRestar: () -> Unit,
    formatoMoneda: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(producto.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(formatoMoneda.format(producto.precio), style = MaterialTheme.typography.bodyMedium)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRestar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Remove, "Restar")
                }
                Text("$cantidad", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onAgregar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Add, "Agregar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaCarrito() {
    HuertoHogarTheme {
        PantallaCarrito(navController = rememberNavController())
    }
}
