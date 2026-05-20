package screens

import androidx.compose.animation.AnimatedVisibility
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
import com.example.easyteeth.viewmodel.AppointmentSearcherViewModel
import screens.AppointmentRow
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAppointmentScreen(
    navController: NavController,
    viewModel: AppointmentSearcherViewModel = viewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var appointmentToDelete by remember { mutableStateOf<com.example.easyteeth.model.Appointment?>(null) }

    var expandedTreatment by remember { mutableStateOf(false) }
    var expandedOdonto by remember { mutableStateOf(false) }
    var expandedBox by remember { mutableStateOf(false) }

    // Diálogo del Calendario
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
                            timeZone = TimeZone.getTimeZone("UTC")
                        }
                        viewModel.filterDate = sdf.format(Date(millis))
                        viewModel.applyAllFilters()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Diálogo de confirmación de eliminación
    if (showConfirmDelete && appointmentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmDelete = false
                appointmentToDelete = null
            },
            title = { Text("Confirmar eliminació", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("¿Estás segur de que desitges eliminar aquesta cita?")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Pacient: ${appointmentToDelete?.patient?.name} ${appointmentToDelete?.patient?.lastname1}",
                        fontSize = 13.sp
                    )
                    Text(
                        "Data: ${appointmentToDelete?.date?.split("T")?.get(0)}",
                        fontSize = 13.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        appointmentToDelete?.id?.let { appointmentId ->
                            viewModel.deleteAppointment(appointmentId,
                                onSuccess = {
                                    showConfirmDelete = false
                                    appointmentToDelete = null
                                },
                                onError = {
                                    // Handle error
                                }
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConfirmDelete = false
                        appointmentToDelete = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    val darkBlue = Color(0xFF1B4B7C)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Cites",
                        fontWeight = FontWeight.Bold,
                        color = Color(255,255,255)
                    )
                },

                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = darkBlue
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // BUSCADOR PRINCIPAL POR NOMBRE + BOTÓN DESPLEGABLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.filterPatient,
                    onValueChange = {
                        viewModel.filterPatient = it
                        viewModel.applyAllFilters()
                    },
                    label = { Text("Nom del pacient") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true
                )

                FilledIconButton(
                    onClick = { showFilters = !showFilters },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (showFilters) Color(0xFF1E70EB) else Color(0xFFF1F4F9),
                        contentColor = if (showFilters) Color.White else Color.Black
                    ),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (showFilters) Icons.Default.KeyboardArrowUp else Icons.Default.List,
                        contentDescription = "Filtres"
                    )
                }
            }

            // PANEL DE FILTROS
            AnimatedVisibility(visible = showFilters) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Filtres avançats", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = if(viewModel.filterDate.isEmpty()) Color.Gray else Color(0xFF1E70EB)
                            )
                        ) {
                            Text(if(viewModel.filterDate.isEmpty()) "Escollir Data" else viewModel.filterDate, fontSize = 12.sp)
                        }

                        OutlinedTextField(
                            value = viewModel.filterTime,
                            onValueChange = {
                                viewModel.filterTime = it
                                viewModel.applyAllFilters()
                            },
                            label = { Text("Hora (ej. 10:00)") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
                            singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LISTADO DE RESULTADOS
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF1E70EB))
                } else if (viewModel.filteredAppointments.isEmpty()) {
                    Text(
                        "No s'han trobat cites amb aquestes dates",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(viewModel.filteredAppointments) { appointment ->
                            DeleteAppointmentRow(
                                appointment = appointment,
                                onDelete = {
                                    appointmentToDelete = appointment
                                    showConfirmDelete = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAppointmentRow(
    appointment: com.example.easyteeth.model.Appointment,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F9))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                val hora = appointment.date.split("T").getOrNull(1)?.substring(0, 5) ?: "--:--"
                Text(
                    text = hora,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF323232)
                )
                Text(
                    text = "Box ${appointment.box?.numBox ?: "?"}",
                    fontSize = 12.sp,
                    color = Color(0xFF7499D1)
                )
            }

            Column(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = "${appointment.patient?.name} ${appointment.patient?.lastname1}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = appointment.treatment?.name ?: "Tractament",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = appointment.odontologist?.let { "${it.name} ${it.lastname1}" } ?: "Odontòleg",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.Red
                )
            }
        }
    }
}
