package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.api.*
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch

@Composable
fun NewPatientScreen(
    navController: NavController
) {
    var name by remember { mutableStateOf("") }
    var lastname1 by remember { mutableStateOf("") }
    var lastname2 by remember { mutableStateOf("") }
    var ssn by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Nou Pacient",
                style = MaterialTheme.typography.headlineMedium
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastname1,
                onValueChange = { lastname1 = it },
                label = { Text("Primer cognom") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = lastname2,
                onValueChange = { lastname2 = it },
                label = { Text("Segon cognom") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = ssn,
                onValueChange = { ssn = it },
                label = { Text("SSN") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("DNI") },
                modifier = Modifier.fillMaxWidth()
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        errorMessage = null

                        try {
                            val response = RetrofitClient.patientApi.createPatient(
                                PatientRequest(
                                    name = name.trim(),
                                    lastname1 = lastname1.trim(),
                                    lastname2 = lastname2.trim(),
                                    ssn = ssn.trim(),
                                    dni = dni.trim()
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
                                errorMessage = "Error al guardar el paciente: ${response.code()}"
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Error de conexión"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Guardar pacient")
                }
            }

            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tornar")
            }
        }
    }
}