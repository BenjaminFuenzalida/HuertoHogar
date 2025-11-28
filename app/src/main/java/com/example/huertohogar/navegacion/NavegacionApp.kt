package com.example.huertohogar.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.huertohogar.datos.BaseDeDatosApp
import com.example.huertohogar.viewmodel.AdminViewModel
import com.example.huertohogar.viewmodel.CarritoViewModel
import com.example.huertohogar.viewmodel.CatalogoViewModel
import com.example.huertohogar.viewmodel.LoginViewModel
import com.example.huertohogar.viewmodel.RegistroViewModel
import com.example.huertohogar.vistas.PantallaAdmin
import com.example.huertohogar.vistas.PantallaCarrito
import com.example.huertohogar.vistas.PantallaCatalogo
import com.example.huertohogar.vistas.PantallaEditarProducto
import com.example.huertohogar.vistas.PantallaLogin
import com.example.huertohogar.vistas.PantallaRegistro

/**
 * Gestion de la navegación principal de la App.
 */
@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    val contexto = LocalContext.current.applicationContext
    val db = remember { BaseDeDatosApp.getInstancia(contexto) }
    val productoDao = remember { db.productoDao() }
    val usuarioDao = remember { db.usuarioDao() }
    val carritoDao = remember { db.carritoDao() }

    // --- ViewModels con fábricas seguras ---

    val adminViewModel: AdminViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AdminViewModel(productoDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })

    val carritoViewModel: CarritoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CarritoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CarritoViewModel(carritoDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })

    val loginViewModel: LoginViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(usuarioDao, carritoViewModel) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })

    val catalogoViewModel: CatalogoViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CatalogoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CatalogoViewModel(productoDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })

    val registroViewModel: RegistroViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return RegistroViewModel(usuarioDao) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    })

    NavHost(
        navController = navController,
        startDestination = Rutas.Catalogo.ruta
    ) {
        composable(Rutas.Registro.ruta) {
            PantallaRegistro(navController = navController, viewModel = registroViewModel)
        }

        composable(Rutas.Login.ruta) {
            PantallaLogin(navController = navController, viewModel = loginViewModel)
        }

        composable(Rutas.Catalogo.ruta) {
            PantallaCatalogo(
                navController = navController,
                catalogoViewModel = catalogoViewModel,
                loginViewModel = loginViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable(Rutas.Carrito.ruta) {
            PantallaCarrito(
                navController = navController,
                catalogoViewModel = catalogoViewModel,
                loginViewModel = loginViewModel,
                carritoViewModel = carritoViewModel
            )
        }

        composable(Rutas.Admin.ruta) {
            PantallaAdmin(navController = navController, viewModel = adminViewModel)
        }

        composable(
            route = Rutas.EditarProducto.ruta,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) {
            val productoId = it.arguments?.getInt("productoId")
            PantallaEditarProducto(
                navController = navController, 
                productoId = productoId, 
                adminViewModel = adminViewModel
            )
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
    object Admin : Rutas("admin")
    object EditarProducto : Rutas("editarProducto/{productoId}")
}