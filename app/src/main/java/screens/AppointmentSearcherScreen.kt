package com.example.easyteeth.screens





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
fun AppointmentSearcherScreen(
    navController: NavController,
    viewModel: AppointmentSearcherViewModel = viewModel()
) {
    var showFilters by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    // Estados para controlar la apertura de los desplegables internos
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

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Buscador de Citas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearFilters() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Limpiar Filtros", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
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
                    label = { Text("Nombre del paciente") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    singleLine = true
                )

                // Botón para mostrar/ocultar filtros avanzados
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
                        contentDescription = "Filtros"
                    )
                }
            }

            // PANEL DE FILTROS (Solo visible si showFilters es true)
            AnimatedVisibility(visible = showFilters) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8F9FA), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Filtros avanzados", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)

                    // Fecha y Hora
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
                            Text(if(viewModel.filterDate.isEmpty()) "Elegir Fecha" else viewModel.filterDate, fontSize = 12.sp)
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

                    // Tratamiento y Odontólogo
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MyDropdownMenu(
                            label = "Tratamiento",
                            selectedOption = viewModel.filterTreatment,
                            options = viewModel.treatmentOptions,
                            expanded = expandedTreatment,
                            onExpandedChange = { expandedTreatment = it },
                            onOptionSelected = {
                                viewModel.filterTreatment = it
                                viewModel.applyAllFilters()
                            },
                            modifier = Modifier.weight(1f)
                        )

                        MyDropdownMenu(
                            label = "Odontólogo",
                            selectedOption = viewModel.filterOdontologist,
                            options = viewModel.odontologistOptions,
                            expanded = expandedOdonto,
                            onExpandedChange = { expandedOdonto = it },
                            onOptionSelected = {
                                viewModel.filterOdontologist = it
                                viewModel.applyAllFilters()
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Box
                    MyDropdownMenu(
                        label = "Número de Box",
                        selectedOption = viewModel.filterBox,
                        options = viewModel.boxOptions,
                        expanded = expandedBox,
                        onExpandedChange = { expandedBox = it },
                        onOptionSelected = {
                            viewModel.filterBox = it
                            viewModel.applyAllFilters()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LISTADO DE RESULTADOS
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF1E70EB))
                } else if (viewModel.filteredAppointments.isEmpty()) {
                    Text(
                        "No se encontraron citas con estos filtros",
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
                            // Asegúrate de tener esta función definida en tu proyecto
                            AppointmentRow(appointment)
                        }
                    }
                }
            }
        }
    }
}

// COMPONENTE AUXILIAR PARA LOS DESPLEGABLES
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDropdownMenu(
    label: String,
    selectedOption: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, fontSize = 11.sp) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(8.dp),
            textStyle = MaterialTheme.typography.bodySmall
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}