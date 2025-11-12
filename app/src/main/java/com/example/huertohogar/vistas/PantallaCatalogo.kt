package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme
import com.example.huertohogar.ui.theme.playfairDisplay
import com.example.huertohogar.viewmodel.CatalogoViewModel

/**
* Pantalla principal (Catálogo).
* Se Muestra los productos y la barra de navegación superior.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(navController: NavController) {

    // --- 1. Obtener mi ViewModel ---
    // Se utiliza la misma "Fábrica" que en PantallaRegistro para obtener la misma instancia de DAO.
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

    // Así se obtiene la instancia del ViewModel.
    // Si "PantallaCarrito" usa esta misma factory, obtendrán la misma instancia.
    val viewModel: CatalogoViewModel = viewModel(factory = factory)

    // --- 2. Observar el Estado ---
    val uiState by viewModel.uiState.collectAsState()
    val carritoConteo = uiState.carrito.values.sum()

    // --- 3. UI con Scaffold ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "HuertoHogar",
                        fontFamily = playfairDisplay,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    TextButton(onClick = { navController.navigate(Rutas.Login.ruta) }) {
                        Text("Iniciar Sesión")
                    }

                    TextButton(onClick = { navController.navigate(Rutas.Registro.ruta) }) {
                        Text("Registro")
                    }


                    // Botón del carrito con el contador.
                    IconButton(onClick = { navController.navigate(Rutas.Carrito.ruta) }) {
                        BadgedBox(
                            badge = {
                                if (carritoConteo > 0) {
                                    Badge { Text("$carritoConteo") }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ShoppingCart,
                                contentDescription = "Carrito de Compras"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.tertiary
                )
            )
        }
    ) { paddingValues ->

        // --- 4. El contenido de la pantalla ---
        Column(modifier = Modifier.padding(paddingValues)) {
            if (uiState.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                // Grilla de productos.
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2), // 2 columnas
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregarClick = {
                                viewModel.agregarAlCarrito(producto)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewPantallaCatalogo() {
    HuertoHogarTheme {
        PantallaCatalogo(navController = rememberNavController())
    }
}
