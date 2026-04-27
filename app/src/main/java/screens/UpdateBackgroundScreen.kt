package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Background
import com.example.easyteeth.model.BackgroundRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualitzar Background", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Tornar",
                            tint = Color.White
                        )
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE3F2FD)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Informació clínica",
                            style = MaterialTheme.typography.titleMedium
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
                                        val response =
                                            RetrofitClient.backgroundApi.updateBackground(
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
                                            errorMessage =
                                                "Error al actualitzar el background: ${response.code()}"
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
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                        ) {
                            Text("Actualitzar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(2.dp, Color(0xFF1B4B7C))
                        ) {
                            Text("Cancel·lar", color = Color(0xFF1B4B7C))
                        }
                    }
                }
            }
        }
    }
}