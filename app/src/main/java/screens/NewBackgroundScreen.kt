package screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.api.*
import com.example.easyteeth.model.BackgroundRequest
import kotlinx.coroutines.launch
import navigation.Routes

@Composable
fun NewBackgroundScreen(
    navController: NavController,
    patientId: Long
) {
    var familyHistory by remember { mutableStateOf("") }
    var healthState by remember { mutableStateOf("") }
    var lifeHabits by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }

    var importantAllergie by remember { mutableStateOf(false) }
    var infectiousDisease by remember { mutableStateOf(false) }
    var hasSignedConsent by remember { mutableStateOf(false) }
    var hasSignedAnesthesia by remember { mutableStateOf(false) }

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
                text = "Nou Background",
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Patient ID: $patientId",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = familyHistory,
                onValueChange = { familyHistory = it },
                label = { Text("Antecedents familiars") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4
            )

            OutlinedTextField(
                value = healthState,
                onValueChange = { healthState = it },
                label = { Text("Estat de salut") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4
            )

            OutlinedTextField(
                value = lifeHabits,
                onValueChange = { lifeHabits = it },
                label = { Text("Hàbits de vida") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4
            )

            OutlinedTextField(
                value = allergies,
                onValueChange = { allergies = it },
                label = { Text("Al·lèrgies") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4
            )

            OutlinedTextField(
                value = medication,
                onValueChange = { medication = it },
                label = { Text("Medicació") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                minLines = 4
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Al·lèrgia important")
                Switch(
                    checked = importantAllergie,
                    onCheckedChange = { importantAllergie = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Malaltia infecciosa")
                Switch(
                    checked = infectiousDisease,
                    onCheckedChange = { infectiousDisease = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Consentiment signat")
                Switch(
                    checked = hasSignedConsent,
                    onCheckedChange = { hasSignedConsent = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Anestèsia signada")
                Switch(
                    checked = hasSignedAnesthesia,
                    onCheckedChange = { hasSignedAnesthesia = it }
                )
            }

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
                            val response = RetrofitClient.backgroundApi.createBackground(
                                BackgroundRequest(
                                    familyHistory = familyHistory.trim(),
                                    healthState = healthState.trim(),
                                    lifeHabits = lifeHabits.trim(),
                                    allergies = allergies.trim(),
                                    medication = medication.trim(),
                                    importantAllergie = importantAllergie,
                                    infectiousDisease = infectiousDisease,
                                    hasSignedConsent = hasSignedConsent,
                                    hasSignedAnesthesia = hasSignedAnesthesia,
                                    patientId = patientId
                                )
                            )

                            if (response.isSuccessful) {
                                navController.navigate(Routes.PATIENT_MENU_SCREEN) {
                                    popUpTo(Routes.PATIENT_MENU_SCREEN) { inclusive = false }
                                }
                            } else {
                                errorMessage = "Error al guardar el background: ${response.code()}"
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
                    Text("Guardar background")
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