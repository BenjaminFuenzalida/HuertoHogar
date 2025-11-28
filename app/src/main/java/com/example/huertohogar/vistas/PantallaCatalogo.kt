package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme
import com.example.huertohogar.ui.theme.playfairDisplay
import com.example.huertohogar.viewmodel.CarritoViewModel
import com.example.huertohogar.viewmodel.CatalogoViewModel
import com.example.huertohogar.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaCatalogo(
    navController: NavController,
    catalogoViewModel: CatalogoViewModel,
    loginViewModel: LoginViewModel,
    carritoViewModel: CarritoViewModel
) {
    val catalogoUiState by catalogoViewModel.uiState.collectAsState()
    val loginUiState by loginViewModel.uiState.collectAsState()
    val carritoUiState by carritoViewModel.uiState.collectAsState()
    val carritoConteo = carritoUiState.items.sumOf { it.cantidad }

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
                    if (loginUiState.isUserLoggedIn) {
                        if (loginUiState.usuario?.isAdmin == true) {
                            TextButton(onClick = { navController.navigate(Rutas.Admin.ruta) }) {
                                Text("Administrar")
                            }
                        }
                        TextButton(onClick = { loginViewModel.logout() }) {
                            Text("Cerrar Sesión")
                        }
                    } else {
                        TextButton(onClick = { navController.navigate(Rutas.Login.ruta) }) {
                            Text("Iniciar Sesión")
                        }
                        TextButton(onClick = { navController.navigate(Rutas.Registro.ruta) }) {
                            Text("Registro")
                        }
                    }

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
        Column(modifier = Modifier.padding(paddingValues)) {
            if (catalogoUiState.cargando) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (catalogoUiState.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${catalogoUiState.error}", color = MaterialTheme.colorScheme.error)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(catalogoUiState.productos) { producto ->
                        ProductoCard(
                            producto = producto,
                            onAgregarClick = {
                                loginUiState.usuario?.let {
                                    carritoViewModel.agregarAlCarrito(it, producto)
                                }
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
        // PantallaCatalogo(navController = rememberNavController(), ...)
    }
}