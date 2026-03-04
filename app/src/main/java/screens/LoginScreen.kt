package screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.R // Asegúrate de que el package sea el correcto
import navigation.Routes
import viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    // --- CONFIGURACIÓN DE BARRA DE ESTADO ---
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo de EasyTeeth
            Image(
                painter = painterResource(id = R.drawable.easy_teeth_sin_fondo),
                contentDescription = "Logo",
                modifier = Modifier.size(250.dp), // Ajustado un poco para que quepa todo
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Bienvenido a tu centro\nde control dental",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp,
                color = Color(0xFF1A1C1E)
            )

            Text(
                text = "Inicia sesión para gestionar la clínica",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Usuario vinculado al ViewModel
            Text(
                text = "USUARIO",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = viewModel.username,
                onValueChange = { viewModel.username = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nombre de usuario") },
                trailingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF8F9FA),
                    focusedContainerColor = Color(0xFFF8F9FA)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo Contraseña vinculado al ViewModel
            Text(
                text = "CONTRASEÑA",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
            )
            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF8F9FA),
                    focusedContainerColor = Color(0xFFF8F9FA)
                )
            )

            // Botón de olvido de contraseña (puedes vincularlo a un registro si quieres)
            TextButton(
                onClick = { /* Navegar a registro o recuperación */ },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("¿Contraseña olvidada?", color = Color(0xFF1E70EB), fontSize = 12.sp)
            }

            // Mensaje de error dinámico desde el ViewModel
            viewModel.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Iniciar Sesión con lógica de Retrofit
            Button(
                onClick = {
                    viewModel.onLoginClick { user ->
                        // Usamos la constante de tu archivo Routes
                        navController.navigate(Routes.HOME) {
                            // Borramos el historial para que no pueda volver al login con el botón atrás
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70EB)),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Separador visual
            Row(verticalAlignment = Alignment.CenterVertically) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
                Text("  O CONTINÚA CON  ", fontSize = 10.sp, color = Color.Gray)
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Botones Sociales (Estéticos)
            Row(modifier = Modifier.fillMaxWidth()) {
                SocialButton(
                    iconRes = R.drawable.google, // Asegúrate de tener estos iconos en res/drawable
                    text = "Google",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SocialButton(
                    iconRes = R.drawable.apple,
                    text = "Apple",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SocialButton(iconRes: Int, text: String, modifier: Modifier = Modifier) {
    OutlinedButton(
        onClick = { /* Opcional: Implementar más adelante */ },
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.LightGray)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, color = Color.Black, fontSize = 14.sp)
        }
    }
}