package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme

/**
* Pantalla de Login. Por ahora es un placeholder.
*/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaLogin(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Iniciar Sesión") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¡Bienvenido de nuevo!",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    // Al loguearme, vuelvo a la pantalla anterior (que seria el carrito).
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }

            TextButton(onClick = { navController.navigate(Rutas.Registro.ruta) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaLogin() {
    HuertoHogarTheme {
        PantallaLogin(navController = rememberNavController())
    }
}
