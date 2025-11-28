package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogar.modelos.Producto
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme
import com.example.huertohogar.viewmodel.CarritoViewModel
import com.example.huertohogar.viewmodel.CatalogoViewModel
import com.example.huertohogar.viewmodel.LoginViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCarrito(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel,
    loginViewModel: LoginViewModel,
    carritoViewModel: CarritoViewModel
) {
    val catalogoUiState by catalogoViewModel.uiState.collectAsState()
    val loginUiState by loginViewModel.uiState.collectAsState()
    val carritoUiState by carritoViewModel.uiState.collectAsState()

    val productosEnCarrito = remember(catalogoUiState.productos, carritoUiState.items) {
        catalogoUiState.productos.filter { producto ->
            carritoUiState.items.any { it.productoId == producto.id }
        }
    }

    val totalCarrito = remember(productosEnCarrito, carritoUiState.items) {
        productosEnCarrito.sumOf { producto ->
            val item = carritoUiState.items.find { it.productoId == producto.id }
            val cantidad = item?.cantidad ?: 0
            producto.precio * cantidad
        }
    }

    val formatoMoneda = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
    formatoMoneda.maximumFractionDigits = 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carrito") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        bottomBar = {
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
                            if (loginUiState.isUserLoggedIn) {
                                // Lógica para finalizar la compra (futuro)
                            } else {
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
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
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
                    val item = carritoUiState.items.find { it.productoId == producto.id }
                    val cantidad = item?.cantidad ?: 0
                    ItemCarrito(
                        producto = producto,
                        cantidad = cantidad,
                        onAgregar = { loginUiState.usuario?.let { carritoViewModel.agregarAlCarrito(it, producto) } },
                        onRestar = { loginUiState.usuario?.let { carritoViewModel.eliminarDelCarrito(it, producto) } },
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
                Text(
                    producto.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    formatoMoneda.format(producto.precio),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRestar, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Filled.Remove, "Restar")
                }
                Text(
                    "$cantidad",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
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
        // PantallaCarrito(navController = rememberNavController(), ...)
    }
}