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
import com.example.easyteeth.model.Background
import com.example.easyteeth.model.BackgroundRequest
import kotlinx.coroutines.launch

@Composable
fun UpdateBackgroundScreen(
    navController: NavController,
    patientId: Long
) {
    var backgroundId by remember { mutableStateOf<Long?>(null) }

    var familyHistory by remember { mutableStateOf("") }
    var healthState by remember { mutableStateOf("") }
    var lifeHabits by remember { mutableStateOf("") }
    var allergies by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }

    var importantAllergie by remember { mutableStateOf(false) }
    var infectiousDisease by remember { mutableStateOf(false) }
    var hasSignedConsent by remember { mutableStateOf(false) }
    var hasSignedAnesthesia by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.backgroundApi.getBackgroundsByPatientId(patientId)

                if (response.isSuccessful) {
                    val background = response.body()?.firstOrNull()

                    if (background != null) {
                        backgroundId = background.id
                        familyHistory = background.familyHistory
                        healthState = background.healthState
                        lifeHabits = background.lifeHabits
                        allergies = background.allergies
                        medication = background.medication
                        importantAllergie = background.importantAllergie
                        infectiousDisease = background.infectiousDisease
                        hasSignedConsent = background.hasSignedConsent
                        hasSignedAnesthesia = background.hasSignedAnesthesia
                    } else {
                        errorMessage = "Aquest pacient no té background"
                    }
                } else {
                    errorMessage = "Error al carregar el background: ${response.code()}"
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
                text = "Actualitzar Background",
                style = MaterialTheme.typography.headlineMedium
            )

            if (isLoading) {
                CircularProgressIndicator()
            } else {
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
                        val idToUpdate = backgroundId
                        if (idToUpdate == null) {
                            errorMessage = "No hi ha background per actualitzar"
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            try {
                                val response = RetrofitClient.backgroundApi.updateBackground(
                                    idToUpdate,
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
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "Error al actualitzar el background: ${response.code()}"
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