package screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.Appointment
import com.example.easyteeth.model.AppointmentRequest
import com.example.easyteeth.viewmodel.AppointmentSearcherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateAppointmentScreen(
    navController: NavController,
    viewModel: AppointmentSearcherViewModel = viewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    var selectedAppointment by remember { mutableStateOf<Appointment?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Campos editables
    var editMotive by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }
    var editBoxId by remember { mutableStateOf<Long?>(null) }
    var editTreatmentId by remember { mutableStateOf<Long?>(null) }
    var editOdontologistId by remember { mutableStateOf<Long?>(null) }

    var isUpdating by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllAppointments()
    }

    val darkBlue = Color(0xFF1B4B7C)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Actualitzar cita",
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
                .padding(16.dp)
        ) {
            // BUSCADOR
            TextField(
                value = viewModel.filterPatient,
                onValueChange = {
                    viewModel.filterPatient = it
                    viewModel.applyAllFilters()
                },
                label = { Text("Buscar per pacient") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, "") },
                trailingIcon = {
                    if (viewModel.filterPatient.isNotEmpty()) {
                        IconButton(onClick = {
                            viewModel.filterPatient = ""
                            viewModel.applyAllFilters()
                        }) {
                            Icon(Icons.Default.Close, "")
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // BOTÓN DE FILTROS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Filtres avançats", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                IconButton(onClick = { showFilters = !showFilters }) {
                    Icon(
                        imageVector = if (showFilters) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Filtres"
                    )
                }
            }

            // FILTROS EXPANDIBLES
            AnimatedVisibility(visible = showFilters) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    // Filtros aquí (similar a DeleteAppointmentScreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LISTADO
            if (viewModel.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (viewModel.filteredAppointments.isEmpty()) {
                Text(
                    "No s'han trobat cites",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.filteredAppointments) { appointment ->
                        UpdateAppointmentRow(
                            appointment = appointment,
                            onUpdateClick = {
                                selectedAppointment = appointment
                                editMotive = appointment.motive
                                // Parse date and time
                                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                try {
                                    val dateObj = dateFormat.parse(appointment.date)
                                    if (dateObj != null) {
                                        editDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(dateObj)
                                        editTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(dateObj)
                                    }
                                } catch (e: Exception) {
                                    editDate = appointment.date
                                    editTime = ""
                                }
                                editBoxId = appointment.box?.id
                                editTreatmentId = appointment.treatment?.id
                                editOdontologistId = appointment.odontologist?.id
                                showEditDialog = true
                            }
                        )
                    }
                }
            }
        }
    }

    // DIÁLOGO DE EDICIÓN
    if (showEditDialog && selectedAppointment != null) {
        Dialog(
            onDismissRequest = { showEditDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Título
                    Text(
                        "Actualitzar Cita",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Paciente (No editable)
                    Text(
                        "Pacient",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    Text(
                        selectedAppointment!!.patient?.name ?: "N/A",
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // FECHA
                    Text(
                        "Data",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    TextField(
                        value = editDate,
                        onValueChange = { editDate = it },
                        label = { Text("DD/MM/YYYY") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    // HORA
                    Text(
                        "Hora",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    TextField(
                        value = editTime,
                        onValueChange = { editTime = it },
                        label = { Text("HH:mm") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        singleLine = true
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // BOX DROPDOWN
                    var expandedBox by remember { mutableStateOf(false) }
                    val boxList = viewModel.filteredAppointments
                        .mapNotNull { it.box?.id }
                        .distinct()
                        .sorted()

                    Text(
                        "Box",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedBox,
                        onExpandedChange = { expandedBox = it }
                    ) {
                        TextField(
                            value = editBoxId?.toString() ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Box") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBox) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedBox,
                            onDismissRequest = { expandedBox = false }
                        ) {
                            boxList.forEach { boxId ->
                                DropdownMenuItem(
                                    text = { Text("Box $boxId") },
                                    onClick = {
                                        editBoxId = boxId
                                        expandedBox = false
                                    }
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // TREATMENT DROPDOWN
                    var expandedTreatment by remember { mutableStateOf(false) }
                    val treatmentList = viewModel.filteredAppointments
                        .mapNotNull { it.treatment?.let { t -> t.id to t.name } }
                        .distinctBy { it.first }
                        .sortedBy { it.second }

                    Text(
                        "Tractament",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedTreatment,
                        onExpandedChange = { expandedTreatment = it }
                    ) {
                        TextField(
                            value = treatmentList.find { it.first == editTreatmentId }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Tractament") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTreatment) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedTreatment,
                            onDismissRequest = { expandedTreatment = false }
                        ) {
                            treatmentList.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        editTreatmentId = id
                                        expandedTreatment = false
                                    }
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // ODONTOLOGIST DROPDOWN
                    var expandedOdontologist by remember { mutableStateOf(false) }
                    val odontologistList = viewModel.filteredAppointments
                        .mapNotNull { it.odontologist?.let { o -> o.id to "${o.name} ${o.lastname1}" } }
                        .distinctBy { it.first }
                        .sortedBy { it.second }

                    Text(
                        "Odontòleg",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedOdontologist,
                        onExpandedChange = { expandedOdontologist = it }
                    ) {
                        TextField(
                            value = odontologistList.find { it.first == editOdontologistId }?.second ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Odontòleg") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedOdontologist) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedOdontologist,
                            onDismissRequest = { expandedOdontologist = false }
                        ) {
                            odontologistList.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        editOdontologistId = id
                                        expandedOdontologist = false
                                    }
                                )
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // MOTIVE
                    Text(
                        "Motiu de la cita",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray
                    )
                    TextField(
                        value = editMotive,
                        onValueChange = { editMotive = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        minLines = 2
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    // BOTONES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        TextButton(onClick = { showEditDialog = false }) {
                            Text("Cancelar")
                        }
                        Button(
                            onClick = {
                                if (editMotive.isNotEmpty() && selectedAppointment != null) {
                                    val appointmentId = selectedAppointment?.id
                                    if (appointmentId != null) {
                                        // Convertir fecha y hora a formato ISO
                                        val dateFormatInput = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                        val dateObj = dateFormatInput.parse(editDate)
                                        val timeFormatInput = SimpleDateFormat("HH:mm", Locale.getDefault())
                                        val timeObj = timeFormatInput.parse(editTime)

                                        val calendar = Calendar.getInstance()
                                        if (dateObj != null && timeObj != null) {
                                            calendar.time = dateObj
                                            val timeCal = Calendar.getInstance()
                                            timeCal.time = timeObj
                                            calendar.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY))
                                            calendar.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE))
                                        }

                                        val dateFormatOutput = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                                        val finalDate = dateFormatOutput.format(calendar.time)

                                        val updateRequest = AppointmentRequest(
                                            motive = editMotive,
                                            date = finalDate,
                                            patientId = selectedAppointment!!.patient?.id,
                                            boxId = editBoxId,
                                            odontologistId = editOdontologistId,
                                            treatmentId = editTreatmentId
                                        )
                                        viewModel.updateAppointment(
                                            appointmentId = appointmentId,
                                            request = updateRequest,
                                            onSuccess = {
                                                showEditDialog = false
                                                selectedAppointment = null
                                            },
                                            onError = { error ->
                                                android.util.Log.e("UpdateAppointment", error)
                                            }
                                        )
                                    }
                                }
                            },
                            enabled = !isUpdating
                        ) {
                            if (isUpdating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UpdateAppointmentRow(
    appointment: Appointment,
    onUpdateClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val appointmentDate = try {
        dateFormat.parse(appointment.date)?.let { SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(it) }
            ?: appointment.date
    } catch (e: Exception) {
        appointment.date
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = appointmentDate,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E70EB)
                )
                Text(
                    text = "Box ${appointment.box?.id}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = appointment.patient?.name ?: "Paciente N/A",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
                Text(
                    text = appointment.treatment?.name ?: "Tractament N/A",
                    fontSize = 11.sp,
                    color = Color(0xFF1E70EB),
                    fontWeight = FontWeight.Medium
                )
            }
            IconButton(onClick = onUpdateClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Actualitzar",
                    tint = Color(0xFF1E70EB)
                )
            }
        }
    }
}
