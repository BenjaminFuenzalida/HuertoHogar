package com.example.huertohogar.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.huertohogar.vistas.PantallaRegistro
import com.example.huertohogar.vistas.PantallaLogin
import com.example.huertohogar.vistas.PantallaCatalogo
import com.example.huertohogar.vistas.PantallaCarrito

/**
* Gestion de la navegación principal de la App.
*/
@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        // Defino la pantalla inicial.
        startDestination = Rutas.Catalogo.ruta
    ) {
        // Defino cada "ruta" y qué pantalla (Composable) se mostrará.

        composable(Rutas.Registro.ruta) {
            PantallaRegistro(navController = navController)
        }

        composable(Rutas.Login.ruta) {
                PantallaLogin(navController = navController)
        }

        composable(Rutas.Catalogo.ruta) {
            PantallaCatalogo(navController = navController)
        }

        composable(Rutas.Carrito.ruta) {
            PantallaCarrito(navController = navController)
        }

    }
}

/**
* Objeto para centralizar los nombres de mis rutas.
*/
sealed class Rutas(val ruta: String) {
    object Registro : Rutas("registro")
    object Login : Rutas("login")
    object Catalogo : Rutas("catalogo")
    object Carrito : Rutas("carrito")
}
