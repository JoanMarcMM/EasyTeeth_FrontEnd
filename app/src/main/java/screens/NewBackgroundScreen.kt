package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.BackgroundRequest
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch
import navigation.Routes
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewBackgroundScreen(
    navController: NavController,
    patientId: Long,
    patientData: String? = null
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
    
    // Deserialize patient data if it's a draft
    var draftPatient by remember { 
        mutableStateOf<PatientRequest?>(
            if (patientData != null && patientId == 0L) {
                val decodedData = URLDecoder.decode(patientData, StandardCharsets.UTF_8.toString())
                deserializePatientRequest(decodedData)
            } else null
        )
    }

    val scope = rememberCoroutineScope()
    val cardShape = RoundedCornerShape(10.dp)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nou Background", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1B4B7C)
                )
            )
        },
        containerColor = Color.White
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
                    text = "Nou background",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Historial mèdic i informació rellevant del pacient",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            HorizontalDivider()

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = cardShape,
                tonalElevation = 1.dp,
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

                    BackgroundTextArea(
                        value = familyHistory,
                        onValueChange = { familyHistory = it },
                        label = "Antecedents familiars"
                    )

                    BackgroundTextArea(
                        value = healthState,
                        onValueChange = { healthState = it },
                        label = "Estat de salut"
                    )

                    BackgroundTextArea(
                        value = lifeHabits,
                        onValueChange = { lifeHabits = it },
                        label = "Hàbits de vida"
                    )

                    BackgroundTextArea(
                        value = allergies,
                        onValueChange = { allergies = it },
                        label = "Al·lèrgies"
                    )

                    BackgroundTextArea(
                        value = medication,
                        onValueChange = { medication = it },
                        label = "Medicació"
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = cardShape,
                tonalElevation = 1.dp,
                color = Color(0xFFE3F2FD)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "Validacions i consentiments",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    SwitchRow(
                        title = "Al·lèrgia important",
                        checked = importantAllergie,
                        onCheckedChange = { importantAllergie = it }
                    )

                    SwitchRow(
                        title = "Malaltia infecciosa",
                        checked = infectiousDisease,
                        onCheckedChange = { infectiousDisease = it }
                    )

                    SwitchRow(
                        title = "Consentiment signat",
                        checked = hasSignedConsent,
                        onCheckedChange = { hasSignedConsent = it }
                    )

                    SwitchRow(
                        title = "Anestèsia signada",
                        checked = hasSignedAnesthesia,
                        onCheckedChange = { hasSignedAnesthesia = it }
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
                            isLoading = true
                            errorMessage = null

                            try {
                                var finalPatientId = patientId

                                // If patientId is 0, we need to create the patient first
                                if (patientId == 0L) {
                                    if (draftPatient == null) {
                                        errorMessage = "Error: no s'han trobat les dades del pacient"
                                        return@launch
                                    }

                                    val patientResponse = RetrofitClient.patientApi.createPatient(draftPatient!!)
                                    if (patientResponse.isSuccessful) {
                                        val createdPatient = patientResponse.body()
                                        finalPatientId = createdPatient?.id ?: 0L
                                        if (finalPatientId == 0L) {
                                            errorMessage = "El backend no ha devuelto el ID del paciente"
                                            return@launch
                                        }
                                    } else {
                                        errorMessage = "Error al guardar el pacient: ${patientResponse.code()}"
                                        return@launch
                                    }
                                }

                                // Now create the background with the patient ID
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
                                        patientId = finalPatientId
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
                    enabled = !isLoading,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            strokeWidth = 2.5.dp,
                            color = Color.White,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    } else {
                        Text("Guardar background", color = Color.White, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold)
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
private fun BackgroundTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 110.dp),
        minLines = 4,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent
        )
    )
}

@Composable
private fun SwitchRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,

            )
        }
    }
}

private fun deserializePatientRequest(json: String): PatientRequest? {
    return try {
        // Simple JSON parsing for PatientRequest
        val name = extractJsonValue(json, "name") ?: return null
        val lastname1 = extractJsonValue(json, "lastname1") ?: return null
        val lastname2 = extractJsonValue(json, "lastname2") ?: return null
        val ssn = extractJsonValue(json, "ssn") ?: return null
        val dni = extractJsonValue(json, "dni") ?: return null
        val phoneNumber = extractJsonValue(json, "phoneNumber")
        val email = extractJsonValue(json, "email")
        val billingAddress = extractJsonValue(json, "billingAddress")
        val bankAccountNumber = extractJsonValue(json, "bankAccountNumber")
        val taxIdentificationNumber = extractJsonValue(json, "taxIdentificationNumber")

        PatientRequest(
            name = name,
            lastname1 = lastname1,
            lastname2 = lastname2,
            ssn = ssn,
            dni = dni,
            phoneNumber = phoneNumber,
            email = email,
            billingAddress = billingAddress,
            bankAccountNumber = bankAccountNumber,
            taxIdentificationNumber = taxIdentificationNumber
        )
    } catch (e: Exception) {
        null
    }
}

private fun extractJsonValue(json: String, key: String): String? {
    val pattern = "\"$key\":\"?([^,}]*)\"?".toRegex()
    val matchResult = pattern.find(json)
    return matchResult?.groupValues?.get(1)?.let {
        if (it == "null") null else unescapeJson(it.trim('"'))
    }
}

private fun unescapeJson(str: String): String {
    return str.replace("\\\"", "\"")
        .replace("\\\\", "\\")
        .replace("\\n", "\n")
        .replace("\\r", "\r")
        .replace("\\t", "\t")
}