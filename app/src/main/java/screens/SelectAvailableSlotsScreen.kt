package screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import viewmodel.SelectAvailableSlotsViewModel
import navigation.Routes
import viewmodel.AppointmentSlot
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectAvailableSlotsScreen(
    patientId: Long,
    treatmentId: Long,
    odontologistId: Long,
    motive: String,
    shift: String,
    boxId: Long,
    hasMedicalAlert: Boolean = false,
    navController: NavController,
    viewModel: SelectAvailableSlotsViewModel = viewModel()
) {
    // Inicializar ViewModel
    LaunchedEffect(patientId, odontologistId, shift) {
        viewModel.initialize(patientId, treatmentId, odontologistId, motive, shift, boxId, hasMedicalAlert)
    }

    var showConfirmationDialog by remember { mutableStateOf(false) }
    var showAutoSuggestionDialog by remember { mutableStateOf(false) }
    var suggestedSlot by remember { mutableStateOf<Pair<LocalDate, AppointmentSlot>?>(null) }

    // Logic to trigger the auto-suggestion dialog
    LaunchedEffect(viewModel.isLoadingSlots, viewModel.availableSlots.size) {
        if (!viewModel.isLoadingSlots && viewModel.availableSlots.isNotEmpty() && suggestedSlot == null) {
            // Buscar el primer día que tenga huecos disponibles
            val firstDayWithSlots = viewModel.availableSlots.firstOrNull { day ->
                day.timeSlots.any { ts -> ts.appointmentSlots.any { it.available } }
            }

            if (firstDayWithSlots != null) {
                val allAvailableSlotsInDay = firstDayWithSlots.timeSlots
                    .flatMap { it.appointmentSlots }
                    .filter { it.available }

                if (allAvailableSlotsInDay.isNotEmpty()) {
                    val targetSlot = if (hasMedicalAlert) {
                        allAvailableSlotsInDay.last()
                    } else {
                        allAvailableSlotsInDay.first()
                    }
                    suggestedSlot = Pair(firstDayWithSlots.date, targetSlot)
                    showAutoSuggestionDialog = true
                }
            }
        }
    }

    // Auto-Suggestion Dialog
    if (showAutoSuggestionDialog && suggestedSlot != null) {
        AlertDialog(
            onDismissRequest = { showAutoSuggestionDialog = false },
            title = { Text("Suggeriment de cita", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Hem trobat el millor buit per a tu d'acord amb la teva selecció:", modifier = Modifier.padding(bottom = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data: ${formatDate(suggestedSlot!!.first)}",
                        fontSize = 14.sp
                    )
                    Text(
                        "Hora: ${suggestedSlot!!.second.slotStart} - ${suggestedSlot!!.second.slotEnd}",
                        fontSize = 14.sp
                    )
                    Text(
                        "Duració: 55 minuts",
                        fontSize = 14.sp
                    )
                    
                    if (hasMedicalAlert) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "Protocol mèdic aplicat (últim torn del dia).",
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.selectedDate = suggestedSlot!!.first
                        viewModel.selectedAppointmentSlot = suggestedSlot!!.second
                        showAutoSuggestionDialog = false
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70EB))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showAutoSuggestionDialog = false },
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Text("Manualment", color = Color.Black)
                }
            }
        )
    }

    // Show confirmation dialog when a slot is selected
    LaunchedEffect(viewModel.selectedAppointmentSlot) {
        if (viewModel.selectedAppointmentSlot != null) {
            showConfirmationDialog = true
        }
    }

    // Confirmation Dialog
    if (showConfirmationDialog && viewModel.selectedAppointmentSlot != null) {
        AlertDialog(
            onDismissRequest = {
                showConfirmationDialog = false
                viewModel.selectedAppointmentSlot = null
            },
            title = { Text("Confirmar Cita", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("¿Vols confirmar aquesta cita?", modifier = Modifier.padding(bottom = 8.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Data: ${formatDate(viewModel.selectedDate ?: java.time.LocalDate.now())}",
                        fontSize = 14.sp
                    )
                    Text(
                        "Hora: ${viewModel.selectedAppointmentSlot?.slotStart ?: ""} - ${viewModel.selectedAppointmentSlot?.slotEnd ?: ""}",
                        fontSize = 14.sp
                    )
                    Text(
                        "Duració: 55 minuts",
                        fontSize = 14.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.createAppointment(
                            onSuccess = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.HOME) { inclusive = true }
                                }
                            },
                            onError = { error ->
                                viewModel.errorMessage = error
                            }
                        )
                    },
                    enabled = !viewModel.isCreating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70EB))
                ) {
                    if (viewModel.isCreating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 1.dp
                        )
                    } else {
                        Text("Confirmar", color = Color.White)
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showConfirmationDialog = false
                        viewModel.selectedAppointmentSlot = null
                    },
                    enabled = !viewModel.isCreating
                ) {
                    Text("Cancel·lar")
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
                        text = "Seleccionar disponibilitat",
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
        when {
            viewModel.isLoadingSlots || viewModel.isLoadingBoxes -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1E70EB))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Carregant disponibilitat...", color = Color.Gray)
                    }
                }
            }
            viewModel.errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Error",
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            viewModel.errorMessage ?: "Error desconegut",
                            textAlign = TextAlign.Center,
                            color = Color.Red,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { navController.popBackStack() }) {
                            Text("Tornar enrere")
                        }
                    }
                }
            }
            viewModel.availableSlots.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Icon(
                            Icons.Default.EventBusy,
                            contentDescription = "No hi ha disponibilitat",
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Sense disponibilitat",
                            textAlign = TextAlign.Center,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Aquest odontòleg no té disponibilitat en els següents 30 dies",
                            textAlign = TextAlign.Center,
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        if (hasMedicalAlert) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.Red, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        "Protocol mèdic: Només es permet l'últim torn disponible.",
                                        color = Color.Red,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text("Próxima disponibilitat", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    // Mostrar día disponible
                    items(viewModel.availableSlots) { daySlots ->
                        val lastAvailableSlotInDay = remember(daySlots) {
                            daySlots.timeSlots.flatMap { it.appointmentSlots }.lastOrNull { it.available }
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectedDate = daySlots.date
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (viewModel.selectedDate == daySlots.date)
                                    Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                            ),
                            border = if (viewModel.selectedDate == daySlots.date)
                                androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E70EB))
                            else null
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            daySlots.dayOfWeek.capitalize(),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Text(
                                            formatDate(daySlots.date),
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    if (viewModel.selectedDate == daySlots.date) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Seleccionat",
                                            tint = Color(0xFF1E70EB)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Mostrar horarios disponibles
                                Text(
                                    "Horaris disponibles:",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    daySlots.timeSlots.forEach { timeSlot ->
                                        Column {
                                            // Period header
                                            Text(
                                                timeSlot.period,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )

                                            // Appointment slots grid (2 columns)
                                            if (timeSlot.appointmentSlots.isNotEmpty()) {
                                                Column(
                                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    // Group slots in pairs for 2-column layout
                                                    timeSlot.appointmentSlots.chunked(2).forEach { pair ->
                                                        Row(
                                                            modifier = Modifier
                                                                .fillMaxWidth(),
                                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                        ) {
                                                            pair.forEach { slot ->
                                                                val isSelected = viewModel.selectedDate == daySlots.date && viewModel.selectedAppointmentSlot == slot
                                                                val isEnabled = slot.available && (!hasMedicalAlert || slot == lastAvailableSlotInDay)

                                                                Button(
                                                                    onClick = {
                                                                        if (slot.available) {
                                                                            viewModel.selectedDate = daySlots.date
                                                                            viewModel.selectedAppointmentSlot = slot
                                                                        }
                                                                    },
                                                                    modifier = Modifier
                                                                        .weight(1f)
                                                                        .height(56.dp),
                                                                    enabled = isEnabled,
                                                                    shape = RoundedCornerShape(8.dp),
                                                                    colors = ButtonDefaults.buttonColors(
                                                                        containerColor = if (isSelected)
                                                                            Color(0xFF1E70EB) else Color(0xFFE3F2FD),
                                                                        contentColor = if (isSelected)
                                                                            Color.White else Color(0xFF1E70EB),
                                                                        disabledContainerColor = Color(0xFFEEEEEE),
                                                                        disabledContentColor = Color.Gray
                                                                    ),
                                                                    border = if (isSelected)
                                                                        androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF1E70EB))
                                                                    else
                                                                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
                                                                ) {
                                                                    Column(
                                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                                        verticalArrangement = Arrangement.Center
                                                                    ) {
                                                                        Text(
                                                                            "${slot.slotStart} - ${slot.slotEnd}",
                                                                            fontSize = 14.sp,
                                                                            fontWeight = FontWeight.Bold
                                                                        )
                                                                        Text(
                                                                            "55 min",
                                                                            fontSize = 10.sp
                                                                        )
                                                                    }
                                                                }
                                                            }

                                                            // Add spacer if only one item in pair
                                                            if (pair.size == 1) {
                                                                Spacer(modifier = Modifier.weight(1f))
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                // Fallback: Show period in text if no individual slots
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clip(RoundedCornerShape(8.dp)),
                                                    color = Color(0xFFE3F2FD),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(
                                                        "${timeSlot.startTime} - ${timeSlot.endTime}",
                                                        modifier = Modifier.padding(12.dp),
                                                        fontSize = 13.sp,
                                                        color = Color(0xFF1E70EB)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

fun formatDate(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return date.format(formatter)
}
