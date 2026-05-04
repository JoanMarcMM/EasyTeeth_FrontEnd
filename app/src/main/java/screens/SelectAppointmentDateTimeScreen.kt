package screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.Box
import com.example.easyteeth.model.Odontologist
import viewmodel.SelectAppointmentDateTimeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAppointmentDateTimeScreen(
    patientId: Long,
    treatmentId: Long,
    odontologistId: Long,
    boxId: Long,
    motive: String,
    navController: NavController,
    viewModel: SelectAppointmentDateTimeViewModel = viewModel()
) {
    // Inicializar ViewModel
    LaunchedEffect(patientId, treatmentId) {
        viewModel.initialize(patientId, treatmentId, odontologistId, boxId, motive)
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var expandedOdontologist by remember { mutableStateOf(false) }
    var expandedBox by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Agendar Cita", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Text("Paso 3: Detalles de la Cita", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // FECHA
            item {
                Column {
                    Text("Fecha:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    OutlinedButton(
                        onClick = { showDatePicker = !showDatePicker },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            viewModel.selectedDateTime?.let {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it)
                            } ?: "Selecciona fecha",
                            color = if (viewModel.selectedDateTime != null) Color.Black else Color.Gray
                        )
                    }
                }
            }

            // HORA
            item {
                if (viewModel.selectedDateTime != null) {
                    Column {
                        Text("Hora:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = viewModel.selectedHour,
                                onValueChange = { if (it.length <= 2) viewModel.selectedHour = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                placeholder = { Text("HH") },
                                singleLine = true
                            )
                            Text(":", fontSize = 20.sp)
                            OutlinedTextField(
                                value = viewModel.selectedMinute,
                                onValueChange = { if (it.length <= 2) viewModel.selectedMinute = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp),
                                placeholder = { Text("MM") },
                                singleLine = true
                            )
                        }
                    }
                }
            }

            // ODONTÓLOGO
            item {
                Column {
                    Text("Odontólogo:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text(
                        "Especializados en este tratamiento",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Box {
                        if (viewModel.isLoadingOdontologists) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(Color(0xFFE3F2FD), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color(0xFF1E70EB),
                                    strokeWidth = 2.dp
                                )
                            }
                        } else if (viewModel.availableOdontologists.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "Sin odontólogos disponibles",
                                    color = Color.Red,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = { expandedOdontologist = !expandedOdontologist },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (viewModel.selectedOdontologist != null)
                                        Color(0xFFE3F2FD) else Color.White
                                )
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        viewModel.selectedOdontologist?.name ?: "Selecciona odontólogo (${viewModel.availableOdontologists.size})",
                                        color = if (viewModel.selectedOdontologist != null) Color.Black else Color.Gray
                                    )
                                    Icon(
                                        if (expandedOdontologist) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
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
                                        text = { Text(odontologist.name) },
                                        onClick = {
                                            viewModel.selectedOdontologist = odontologist
                                            expandedOdontologist = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // CAJA
            item {
                Column {
                    Text("Caja:", fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Box {
                        OutlinedButton(
                            onClick = { expandedBox = !expandedBox },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (viewModel.selectedBox != null)
                                    Color(0xFFE3F2FD) else Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    viewModel.selectedBox?.let { "Caja ${it.numBox}" } ?: "Selecciona caja",
                                    color = if (viewModel.selectedBox != null) Color.Black else Color.Gray
                                )
                                Icon(
                                    if (expandedBox) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = expandedBox,
                            onDismissRequest = { expandedBox = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            viewModel.availableBoxes.forEach { box ->
                                DropdownMenuItem(
                                    text = { Text("Caja ${box.numBox}") },
                                    onClick = {
                                        viewModel.selectedBox = box
                                        expandedBox = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // BOTONES
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            viewModel.createAppointment(
                                onSuccess = {
                                    navController.popBackStack()
                                    navController.popBackStack()
                                },
                                onError = { error ->
                                    // Mostrar error
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = viewModel.isReadyToProceed() && !viewModel.isCreating,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70EB))
                    ) {
                        if (viewModel.isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Crear Cita", fontWeight = FontWeight.Bold)
                        }
                    }

                    OutlinedButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }
    }
}
