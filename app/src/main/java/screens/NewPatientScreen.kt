package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPatientScreen(
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var lastname1 by remember { mutableStateOf("") }
    var lastname2 by remember { mutableStateOf("") }
    var ssn by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var billingAddress by remember { mutableStateOf("") }
    var bankAccountNumber by remember { mutableStateOf("") }
    var taxIdentificationNumber by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val shape = RoundedCornerShape(10.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nou Pacient", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B4B7C)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nou pacient",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Introdueix les dades personals, de contacte i de facturació.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Dades personals",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PatientTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = "Nom"
                    )

                    PatientTextField(
                        value = lastname1,
                        onValueChange = { lastname1 = it },
                        label = "Primer cognom"
                    )

                    PatientTextField(
                        value = lastname2,
                        onValueChange = { lastname2 = it },
                        label = "Segon cognom"
                    )

                    PatientTextField(
                        value = dni,
                        onValueChange = { dni = it },
                        label = "DNI"
                    )

                    PatientTextField(
                        value = ssn,
                        onValueChange = { ssn = it },
                        label = "SSN"
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Contacte",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PatientTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = "Telèfon"
                    )

                    PatientTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email"
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Facturació",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    PatientTextField(
                        value = billingAddress,
                        onValueChange = { billingAddress = it },
                        label = "Adreça de facturació"
                    )

                    PatientTextField(
                        value = bankAccountNumber,
                        onValueChange = { bankAccountNumber = it },
                        label = "Compte bancari"
                    )

                    PatientTextField(
                        value = taxIdentificationNumber,
                        onValueChange = { taxIdentificationNumber = it },
                        label = "NIF / CIF"
                    )
                }
            }

            errorMessage?.let {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            // Validate email
                            if (!isValidEmail(email)) {
                                errorMessage = "El correu electrònic no és vàlid. Utilitza el format: exemple@domini.com"
                                return@launch
                            }

                            // Validate phone number
                            if (!isValidPhoneNumber(phoneNumber)) {
                                errorMessage = "El telèfon no és vàlid. Ha de tenir almenys 6 dígits (opcional: +)"
                                return@launch
                            }

                            isLoading = true
                            errorMessage = null

                            try {
                                val response = RetrofitClient.patientApi.createPatient(
                                    PatientRequest(
                                        name = name.trim(),
                                        lastname1 = lastname1.trim(),
                                        lastname2 = lastname2.trim(),
                                        ssn = ssn.trim(),
                                        dni = dni.trim(),
                                        phoneNumber = phoneNumber.ifBlank { null },
                                        email = email.ifBlank { null },
                                        billingAddress = billingAddress.ifBlank { null },
                                        bankAccountNumber = bankAccountNumber.ifBlank { null },
                                        taxIdentificationNumber = taxIdentificationNumber.ifBlank { null }
                                    )
                                )

                                if (response.isSuccessful) {
                                    val createdPatient = response.body()
                                    val patientId = createdPatient?.id

                                    if (patientId != null) {
                                        navController.navigate("newPatientBackground/$patientId")
                                    } else {
                                        errorMessage = "El backend no ha devuelto el ID del paciente"
                                    }
                                } else {
                                    errorMessage = "Error al guardar el pacient: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Error de connexió"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            color = Color.White
                        )
                    } else {
                        Text("Guardar pacient", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
                    }
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF1B4B7C)
                    ),
                    border = BorderStroke(2.dp, Color(0xFF1B4B7C))
                ) {
                    Text("Tornar")
                }
            }
        }
    }
}

@Composable
private fun PatientTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

private fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return true // Email is optional
    val emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    return email.matches(Regex(emailPattern))
}

private fun isValidPhoneNumber(phoneNumber: String): Boolean {
    if (phoneNumber.isBlank()) return true // Phone is optional
    val phonePattern = "^[+]?[0-9]{6,}$"
    return phoneNumber.matches(Regex(phonePattern))
}