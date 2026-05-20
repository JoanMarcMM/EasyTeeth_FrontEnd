package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.Pathology
import com.example.easyteeth.model.Treatment
import navigation.Routes
import viewmodel.AddAppointmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAppointmentDetailsScreen(
    patientId: Long,
    navController: NavController,
    viewModel: AddAppointmentViewModel = viewModel(
        viewModelStoreOwner = LocalViewModelStoreOwner.current!!
    )
) {
    // Inicializar ViewModel con el patientId
    LaunchedEffect(patientId) {
        viewModel.initialize(patientId)
    }

    var expandedPathology by remember { mutableStateOf(false) }
    var expandedOdontologist by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Crear Cita", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {

            // Mostrar errores si los hay
            if (viewModel.errorMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Text(
                        text = viewModel.errorMessage ?: "",
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp
                    )
                }
            }

            // Indicador de carga general
            if (viewModel.isLoadingPathologies) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF1E70EB))
                }
                return@Column
            }

            // Paso 1: Seleccionar Patología
            Text(
                text = "Paso 1: Selecciona Patología",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Dropdown de Patologías
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                OutlinedButton(
                    onClick = { expandedPathology = !expandedPathology },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (viewModel.selectedPathology != null)
                            Color(0xFFE3F2FD) else Color.White
                    ),
                    border = BorderStroke(
                        2.dp,
                        if (viewModel.selectedPathology != null)
                            Color(0xFF1E70EB) else Color.LightGray
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = viewModel.selectedPathology?.name ?: "Selecciona una patología",
                            color = if (viewModel.selectedPathology != null)
                                Color.Black else Color.Gray,
                            fontSize = 14.sp
                        )
                        Icon(
                            imageVector = if (expandedPathology)
                                Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = Color(0xFF1E70EB)
                        )
                    }
                }

                // Dropdown Menu
                DropdownMenu(
                    expanded = expandedPathology,
                    onDismissRequest = { expandedPathology = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    if (viewModel.patientPathologies.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("Sin patologías registradas", color = Color.Gray) },
                            onClick = {}
                        )
                    } else {
                        viewModel.patientPathologies.forEach { pathology ->
                            DropdownMenuItem(
                                text = { Text(pathology.name) },
                                onClick = {
                                    viewModel.onPathologySelected(pathology)
                                    expandedPathology = false
                                }
                            )
                        }
                    }
                }
            }

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // Paso 2: Seleccionar Tratamiento
            if (viewModel.selectedPathology != null) {
                Text(
                    text = "Paso 2: Selecciona Tratamiento",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "Tratamientos disponibles para ${viewModel.selectedPathology?.name}: ${viewModel.filteredTreatments.size}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Box(modifier = Modifier.weight(1f)) {
                    if (viewModel.isLoadingPathologyTreatments) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF1E70EB)
                        )
                    } else if (viewModel.filteredTreatments.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.LightGray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No hay tratamientos disponibles para esta patología",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(bottom = 100.dp)
                        ) {
                            items(viewModel.filteredTreatments) { treatment ->
                                TreatmentCard(
                                    treatment = treatment,
                                    isSelected = viewModel.selectedTreatment?.id == treatment.id,
                                    onSelected = { viewModel.onTreatmentSelected(treatment) }
                                )
                            }
                        }
                    }
                }

                // Paso 3: Seleccionar Odontólogo (Solo cuando tratamiento está seleccionado)
                if (viewModel.selectedTreatment != null) {
                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Paso 3: Selecciona Odontólogo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Mostrar spinner mientras carga odontólogos
                    if (viewModel.isLoadingOdontologists) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF1E70EB))
                        }
                    } else if (viewModel.availableOdontologists.isEmpty()) {
                        // Mostrar error si no hay odontólogos disponibles
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "No hay odontólogos disponibles para este tratamiento",
                                    color = Color.Gray,
                                    fontSize = 14.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Dropdown de Odontólogos
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { expandedOdontologist = !expandedOdontologist },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (viewModel.selectedOdontologist != null)
                                        Color(0xFFE3F2FD) else Color.White
                                ),
                                border = BorderStroke(
                                    2.dp,
                                    if (viewModel.selectedOdontologist != null)
                                        Color(0xFF1E70EB) else Color.LightGray
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = viewModel.selectedOdontologist?.name
                                            ?: "Selecciona odontólogo (${viewModel.availableOdontologists.size})",
                                        color = if (viewModel.selectedOdontologist != null)
                                            Color.Black else Color.Gray,
                                        fontSize = 14.sp
                                    )
                                    Icon(
                                        imageVector = if (expandedOdontologist)
                                            Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                        contentDescription = null
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = expandedOdontologist,
                                onDismissRequest = { expandedOdontologist = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                viewModel.availableOdontologists.forEach { odontologist ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(odontologist.name ?: "Sin nombre")
                                        },
                                        onClick = {
                                            viewModel.onOdontologistSelected(odontologist)
                                            expandedOdontologist = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Botón Siguiente
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = {
                        val motive = "Tratamiento: ${viewModel.selectedTreatment?.name}, Odontólogo: ${viewModel.selectedOdontologist?.name}"
                        navController.navigate(
                            Routes.selectAppointmentShift(
                                patientId = viewModel.patientId ?: 0L,
                                treatmentId = viewModel.selectedTreatment?.id ?: 0L,
                                odontologistId = viewModel.selectedOdontologist?.id ?: 0L,
                                motive = motive,
                                hasMedicalAlert = viewModel.hasMedicalAlert
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = viewModel.isReadyToProceed(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E70EB),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text("Siguiente", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Componente tarjeta para mostrar un tratamiento
 */
@Composable
fun TreatmentCard(
    treatment: Treatment,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelected() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) Color(0xFF1E70EB) else Color.LightGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = treatment.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = Color.Black
            )

            if (!treatment.description.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = treatment.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}
