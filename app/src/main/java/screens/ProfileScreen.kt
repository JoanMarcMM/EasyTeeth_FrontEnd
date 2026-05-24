package screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import navigation.Routes
import viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    // --- CONFIGURACIÓN DE BARRA DE ESTADO ---
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    var showPasswordForm by remember { mutableStateOf(false) }
    var showConfirmLogout by remember { mutableStateOf(false) }
    var showOldPasswordVisibility by remember { mutableStateOf(false) }
    var showNewPasswordVisibility by remember { mutableStateOf(false) }
    var showConfirmPasswordVisibility by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Perfil de l'Usuari", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B4B7C),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // USER INFO SECTION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Informació de l'Usuari",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Divider(color = Color.LightGray, thickness = 1.dp)

                    viewModel.user?.let { user ->
                        // ID
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ID:",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = user.id?.toString() ?: "N/A",
                                color = Color.Black
                            )
                        }

                        // Username
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Usuari:",
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = user.username,
                                color = Color.Black
                            )
                        }
                    } ?: run {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        } else {
                            Text(
                                text = "No s'ha pogut carregar la informació de l'usuari",
                                color = Color.Red
                            )
                        }
                    }
                }
            }

            // ERROR MESSAGE
            viewModel.errorMessage?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = error,
                        color = Color(0xFFC62828),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // SUCCESS MESSAGE
            viewModel.successMessage?.let { success ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Text(
                        text = success,
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // PASSWORD CHANGE SECTION
            if (!showPasswordForm) {
                Button(
                    onClick = { showPasswordForm = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                ) {
                    Text(
                        text = "Canviar Contrasenya",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            } else {
                // PASSWORD CHANGE FORM
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Canviar Contrasenya",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        // Old Password
                        Text(
                            text = "CONTRASENYA ACTUAL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        OutlinedTextField(
                            value = viewModel.oldPassword,
                            onValueChange = { viewModel.oldPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contrasenya actual") },
                            visualTransformation = if (showOldPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showOldPasswordVisibility = !showOldPasswordVisibility }) {
                                    Text(if (showOldPasswordVisibility) "👁" else "👁‍🗨")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        // New Password
                        Text(
                            text = "CONTRASENYA NOVA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        OutlinedTextField(
                            value = viewModel.newPassword,
                            onValueChange = { viewModel.newPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Contrasenya nova") },
                            visualTransformation = if (showNewPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showNewPasswordVisibility = !showNewPasswordVisibility }) {
                                    Text(if (showNewPasswordVisibility) "👁" else "👁‍🗨")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        // Confirm Password
                        Text(
                            text = "CONFIRMAR CONTRASENYA",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray
                        )
                        OutlinedTextField(
                            value = viewModel.confirmPassword,
                            onValueChange = { viewModel.confirmPassword = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Confirmar contrasenya") },
                            visualTransformation = if (showConfirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { showConfirmPasswordVisibility = !showConfirmPasswordVisibility }) {
                                    Text(if (showConfirmPasswordVisibility) "👁" else "👁‍🗨")
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showPasswordForm = false },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD))
                            ) {
                                Text("Cancel·lar", color = Color(0xFF1B4B7C), fontWeight = FontWeight.SemiBold)
                            }

                            Button(
                                onClick = {
                                    viewModel.changePassword {
                                        showPasswordForm = false
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(45.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C)),
                                enabled = !viewModel.passwordChangeInProgress
                            ) {
                                if (viewModel.passwordChangeInProgress) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    Text("Guardar", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LOGOUT BUTTON
            Button(
                onClick = { showConfirmLogout = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
            ) {
                Text(
                    text = "Tancar la sessió",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showConfirmLogout) {
        AlertDialog(
            onDismissRequest = { showConfirmLogout = false },
            title = { Text("Tancar la sessió") },
            text = { Text("Segur que vols tancar la sessió?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logout {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(Routes.HOME) { inclusive = true }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Tancar la sessió")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmLogout = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Text("Cancel·lar", color = Color(0xFF1B4B7C), fontWeight = FontWeight.SemiBold)
                }
            }
        )
    }
}
