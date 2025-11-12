package com.example.huertohogar.vistas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.huertohogar.navegacion.Rutas
import com.example.huertohogar.ui.theme.HuertoHogarTheme

/**
* Pantalla de Registro de Usuario.
* implementación de:
* - Diseño con Material 3.
* - Formularios con validación.
* - Gestión de estado simple.
* - Navegación funcional.
*/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistro(navController: NavController) {

    // --- Gestión de estado (simple) ---
    var email by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var repetirContrasena by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }

    fun validarYRegistrar() {
        // --- Validación de formulario ---
        if (email.isBlank() || contrasena.isBlank() || repetirContrasena.isBlank()) {
            mensajeError = "Todos los campos son obligatorios"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mensajeError = "El correo no es válido"
            return
        }
        if (contrasena != repetirContrasena) {
            mensajeError = "Las contraseñas no coinciden"
            return
        }
        if (contrasena.length < 6) {
            mensajeError = "La contraseña debe tener al menos 6 caracteres"
            return
        }

        // Si todo es válido:
        mensajeError = null

        navController.navigate(Rutas.Catalogo.ruta) {
            // Se limpia la pila de navegación para no poder volver al registro.
            popUpTo(Rutas.Registro.ruta) { inclusive = true }
        }
    }

    // --- UI (Diseño con Material 3) ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Electrónico") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = repetirContrasena,
            onValueChange = { repetirContrasena = it },
            label = { Text("Repetir Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Se Muestra mensaje de error si existe.
        if (mensajeError != null) {
            Text(
                text = mensajeError!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { validarYRegistrar() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Registrarse", style = MaterialTheme.typography.labelLarge)
        }

        TextButton(onClick = { navController.navigate(Rutas.Login.ruta) }) {
            Text("¿Ya tienes cuenta? Inicia Sesión")
        }
    }
}


// --- Preview para ver el diseño sin ejecutar la app ---
@Preview(showBackground = true)
@Composable
fun PreviewPantallaRegistro() {
    HuertoHogarTheme {
        PantallaRegistro(navController = rememberNavController())
    }
}
