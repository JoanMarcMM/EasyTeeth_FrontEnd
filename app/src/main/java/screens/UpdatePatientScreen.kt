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
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch

@Composable
fun UpdatePatientScreen(
    navController: NavController,
    patientId: Long
) {
    var name by remember { mutableStateOf("") }
    var lastname1 by remember { mutableStateOf("") }
    var lastname2 by remember { mutableStateOf("") }
    var ssn by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.patientApi.getPatientById(patientId)

                if (response.isSuccessful) {
                    val patient = response.body()
                    if (patient != null) {
                        name = patient.name
                        lastname1 = patient.lastname1
                        lastname2 = patient.lastname2
                        ssn = patient.ssn
                        dni = patient.dni
                    } else {
                        errorMessage = "No s'ha pogut carregar el pacient"
                    }
                } else {
                    errorMessage = "Error al carregar el pacient: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error de connexió"
            } finally {
                isLoading = false
            }
        }
    }

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
                text = "Actualitzar Pacient",
                style = MaterialTheme.typography.headlineMedium
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else {
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
                                val response = RetrofitClient.patientApi.updatePatient(
                                    patientId,
                                    PatientRequest(
                                        name = name.trim(),
                                        lastname1 = lastname1.trim(),
                                        lastname2 = lastname2.trim(),
                                        ssn = ssn.trim(),
                                        dni = dni.trim()
                                    )
                                )

                                if (response.isSuccessful) {
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "Error al actualitzar el pacient: ${response.code()}"
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Error de connexió"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                ) {
                    Text("Actualitzar")
                }

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel·lar")
                }
            }
        }
    }
}