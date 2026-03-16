package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Background
import com.example.easyteeth.model.Patient
import kotlinx.coroutines.launch
import navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientProfileScreen(
    navController: NavController,
    patientId: Long
) {
    var patient by remember { mutableStateOf<Patient?>(null) }
    var background by remember { mutableStateOf<Background?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(patientId) {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val patientResponse = RetrofitClient.patientApi.getPatientById(patientId)
                val backgroundResponse = RetrofitClient.backgroundApi.getBackgroundsByPatientId(patientId)

                if (patientResponse.isSuccessful) {
                    patient = patientResponse.body()
                } else {
                    errorMessage = "No se pudo cargar el paciente"
                }

                if (backgroundResponse.isSuccessful) {
                    background = backgroundResponse.body()?.firstOrNull()
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error de conexión"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Perfil del pacient",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7F8FA)
                )
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp)
                    )
                }

                patient != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .imePadding()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Dades del pacient",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                ProfileDataRow("Nom", patient?.name ?: "")
                                ProfileDataRow("Primer cognom", patient?.lastname1 ?: "")
                                ProfileDataRow("Segon cognom", patient?.lastname2 ?: "")
                                ProfileDataRow("DNI", patient?.dni ?: "")
                                ProfileDataRow("SSN", patient?.ssn ?: "")
                                ProfileDataRow("ID", patient?.id?.toString() ?: "")
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Text(
                                    text = "Background",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                if (background != null) {
                                    ProfileDataBlock(
                                        title = "Antecedents familiars",
                                        value = background?.familyHistory ?: ""
                                    )
                                    ProfileDataBlock(
                                        title = "Estat de salut",
                                        value = background?.healthState ?: ""
                                    )
                                    ProfileDataBlock(
                                        title = "Hàbits de vida",
                                        value = background?.lifeHabits ?: ""
                                    )
                                    ProfileDataBlock(
                                        title = "Al·lèrgies",
                                        value = background?.allergies ?: ""
                                    )
                                    ProfileDataBlock(
                                        title = "Medicació",
                                        value = background?.medication ?: ""
                                    )

                                    BooleanInfoRow(
                                        label = "Al·lèrgia important",
                                        value = background?.importantAllergie == true
                                    )
                                    BooleanInfoRow(
                                        label = "Malaltia infecciosa",
                                        value = background?.infectiousDisease == true
                                    )
                                    BooleanInfoRow(
                                        label = "Consentiment signat",
                                        value = background?.hasSignedConsent == true
                                    )
                                    BooleanInfoRow(
                                        label = "Anestèsia signada",
                                        value = background?.hasSignedAnesthesia == true
                                    )
                                } else {
                                    Text(
                                        text = "Aquest pacient no té background registrat.",
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = { navController.navigate("updatePatient/$patientId") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Actualitzar Pacient")
                        }

                        Button(
                            onClick = { navController.navigate("updateBackground/$patientId") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Actualitzar Background")
                        }

                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Eliminar Pacient")
                        }

                        OutlinedButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Tornar")
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    if (showDeleteDialog) {
                        androidx.compose.material3.AlertDialog(
                            onDismissRequest = {
                                if (!isDeleting) showDeleteDialog = false
                            },
                            title = {
                                Text("Eliminar pacient")
                            },
                            text = {
                                Text("Segur que vols eliminar aquest pacient? Aquesta acció no es pot desfer.")
                            },
                            confirmButton = {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            isDeleting = true
                                            errorMessage = null

                                            try {
                                                val response = RetrofitClient.patientApi.deletePatient(patientId)

                                                if (response.isSuccessful) {
                                                    showDeleteDialog = false
                                                    navController.navigate(Routes.PATIENT_LIST_TO_PROFILE) {
                                                        popUpTo(Routes.PATIENT_LIST_TO_PROFILE) { inclusive = true }
                                                        launchSingleTop = true
                                                    }
                                                } else {
                                                    showDeleteDialog = false
                                                    errorMessage = "No s'ha pogut eliminar el pacient: ${response.code()}"
                                                }
                                            } catch (e: Exception) {
                                                showDeleteDialog = false
                                                errorMessage = e.message ?: "Error de connexió"
                                            } finally {
                                                isDeleting = false
                                            }
                                        }
                                    },
                                    enabled = !isDeleting
                                ) {
                                    if (isDeleting) {
                                        CircularProgressIndicator()
                                    } else {
                                        Text("Sí, eliminar")
                                    }
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = { showDeleteDialog = false },
                                    enabled = !isDeleting
                                ) {
                                    Text("Cancel·lar")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDataRow(
    label: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF111827)
        )
    }
}

@Composable
fun ProfileDataBlock(
    title: String,
    value: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF3F4F6)
        ) {
            Text(
                text = value.ifBlank { "-" },
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF111827)
            )
        }
    }
}

@Composable
fun BooleanInfoRow(
    label: String,
    value: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF111827)
        )

        Text(
            text = if (value) "Sí" else "No",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (value) Color(0xFF15803D) else Color(0xFFB91C1C)
        )
    }
}