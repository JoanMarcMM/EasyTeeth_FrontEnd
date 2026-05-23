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
import androidx.compose.foundation.focusable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import utils.Validators

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
                color = Color(0xFFE3F2FD)
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
                        label = "NSS"
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = shape,
                tonalElevation = 1.dp,
                color = Color(0xFFE3F2FD)
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
                color = Color(0xFFE3F2FD)
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

                    OutlinedTextField(
                        value = bankAccountNumber,
                        onValueChange = { newValue ->
                            // Allow user to type without formatting
                            bankAccountNumber = newValue
                        },
                        label = { Text("Compte bancari (IBAN o 16-20 dígits)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { focusState ->
                                // Format only when losing focus
                                if (!focusState.isFocused && bankAccountNumber.isNotEmpty()) {
                                    bankAccountNumber = Validators.formatBankAccountNumber(bankAccountNumber)
                                }
                            },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
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
                            // Validate name fields
                            if (!Validators.isValidName(name)) {
                                errorMessage = "El nom no és vàlid (2-50 caràcters)"
                                return@launch
                            }

                            if (!Validators.isValidName(lastname1)) {
                                errorMessage = "El primer cognom no és vàlid (2-50 caràcters)"
                                return@launch
                            }

                            if (!Validators.isValidName(lastname2)) {
                                errorMessage = "El segon cognom no és vàlid (2-50 caràcters)"
                                return@launch
                            }

                            // Validate DNI
                            if (!Validators.isValidDNI(dni)) {
                                errorMessage = "El DNI no és vàlid. Format: 12345678A o X12345678A"
                                return@launch
                            }

                            // Validate NSS
                            if (!Validators.isValidSSN(ssn)) {
                                errorMessage = "El NSS no és vàlid. Ha de contenir 12 dígits"
                                return@launch
                            }

                            // Validate email
                            if (!Validators.isValidEmail(email)) {
                                errorMessage = "El correu electrònic no és vàlid. Utilitza el format: exemple@domini.com"
                                return@launch
                            }

                            // Validate phone number
                            if (!Validators.isValidPhoneNumber(phoneNumber)) {
                                errorMessage = "El telèfon no és vàlid. Usa format espanyol: 6XX-XXX-XXX, 9XX-XXX-XXX o +34-9XX-XXX-XXX"
                                return@launch
                            }

                            // Validate bank account number
                            if (!Validators.isValidBankAccountNumber(bankAccountNumber)) {
                                errorMessage = "El compte bancari no és vàlid. Usa format IBAN (ES+20 dígits) o 16-20 dígits"
                                return@launch
                            }

                            // Validate NIF/CIF
                            if (!Validators.isValidNIF_CIF(taxIdentificationNumber)) {
                                errorMessage = "El NIF/CIF no és vàlid. Format: 12345678A (NIF) o A12345678 (CIF)"
                                return@launch
                            }

                            isLoading = true
                            errorMessage = null

                            try {
                                // Create patient request and serialize to JSON
                                val patientRequest = PatientRequest(
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
                                
                                // Serialize to JSON and encode for URL
                                val patientJson = serializePatientRequest(patientRequest)
                                val encodedData = URLEncoder.encode(patientJson, StandardCharsets.UTF_8.toString())
                                
                                // Navigate to background screen with patient data
                                navController.navigate("newPatientBackground/0?patientData=$encodedData")
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
    label: String,
    onFocusChanged: ((Boolean) -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                onFocusChanged?.invoke(focusState.isFocused)
            },
        singleLine = true,
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent
        )
    )
}

private fun serializePatientRequest(patient: PatientRequest): String {
    return buildString {
        append("{")
        append("\"name\":\"${escapeJson(patient.name)}\",")
        append("\"lastname1\":\"${escapeJson(patient.lastname1)}\",")
        append("\"lastname2\":\"${escapeJson(patient.lastname2)}\",")
        append("\"ssn\":\"${escapeJson(patient.ssn)}\",")
        append("\"dni\":\"${escapeJson(patient.dni)}\",")
        append("\"phoneNumber\":${patient.phoneNumber?.let { "\"${escapeJson(it)}\"" } ?: "null"},")
        append("\"email\":${patient.email?.let { "\"${escapeJson(it)}\"" } ?: "null"},")
        append("\"billingAddress\":${patient.billingAddress?.let { "\"${escapeJson(it)}\"" } ?: "null"},")
        append("\"bankAccountNumber\":${patient.bankAccountNumber?.let { "\"${escapeJson(it)}\"" } ?: "null"},")
        append("\"taxIdentificationNumber\":${patient.taxIdentificationNumber?.let { "\"${escapeJson(it)}\"" } ?: "null"}")
        append("}")
    }
}

private fun escapeJson(str: String): String {
    return str.replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")
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