package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TextFieldDefaults

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.launch
import utils.Validators

@OptIn(ExperimentalMaterial3Api::class)
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
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var billingAddress by remember { mutableStateOf("") }
    var bankAccountNumber by remember { mutableStateOf("") }
    var taxIdentificationNumber by remember { mutableStateOf("") }

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
                        phoneNumber = patient.phoneNumber ?: ""
                        email = patient.email ?: ""
                        billingAddress = patient.billingAddress ?: ""
                        bankAccountNumber = patient.bankAccountNumber ?: ""
                        taxIdentificationNumber = patient.taxIdentificationNumber ?: ""
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actualitzar Pacient", color = Color.White) },
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
                            text = "Dades personals",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nom") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = lastname1,
                            onValueChange = { lastname1 = it },
                            label = { Text("Primer cognom") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = lastname2,
                            onValueChange = { lastname2 = it },
                            label = { Text("Segon cognom") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = ssn,
                            onValueChange = { ssn = it },
                            label = { Text("NSS") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = dni,
                            onValueChange = { dni = it },
                            label = { Text("DNI") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                    }
                }

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
                            text = "Contacte",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text("Telèfon") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                    }
                }

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
                            text = "Facturació",
                            style = MaterialTheme.typography.titleMedium
                        )

                        OutlinedTextField(
                            value = billingAddress,
                            onValueChange = { billingAddress = it },
                            label = { Text("Adreça de facturació") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = bankAccountNumber,
                            onValueChange = { newValue ->
                                // Allow user to type without formatting
                                bankAccountNumber = newValue
                            },
                            label = { Text("Número de compte (IBAN o 16-20 dígits)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { focusState ->
                                    // Format only when losing focus
                                    if (!focusState.isFocused && bankAccountNumber.isNotEmpty()) {
                                        bankAccountNumber = Validators.formatBankAccountNumber(bankAccountNumber)
                                    }
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )

                        OutlinedTextField(
                            value = taxIdentificationNumber,
                            onValueChange = { taxIdentificationNumber = it },
                            label = { Text("NIF/CIF") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = Color.White,
                                focusedContainerColor = Color.White
                            )
                        )
                    }
                }

                errorMessage?.let {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE)
                    ) {
                        Text(
                            text = it,
                            color = Color(0xFFC62828),
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
                                    val response = RetrofitClient.patientApi.updatePatient(
                                        patientId,
                                        PatientRequest(
                                            name = name.trim(),
                                            lastname1 = lastname1.trim(),
                                            lastname2 = lastname2.trim(),
                                            ssn = ssn.trim(),
                                            dni = dni.trim(),
                                            phoneNumber = phoneNumber.trim().ifBlank { null },
                                            email = email.trim().ifBlank { null },
                                            billingAddress = billingAddress.trim().ifBlank { null },
                                            bankAccountNumber = bankAccountNumber.trim()
                                                .ifBlank { null },
                                            taxIdentificationNumber = taxIdentificationNumber.trim()
                                                .ifBlank { null }
                                        )
                                    )

                                    if (response.isSuccessful) {
                                        navController.popBackStack()
                                    } else {
                                        errorMessage =
                                            "Error al actualitzar el pacient: ${response.code()}"
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
                        Text("Actualitzar", color = Color.White)
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
                        Text("Cancel·lar")
                    }
                }
            }
        }
    }
}
