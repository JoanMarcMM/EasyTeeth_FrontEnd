package screens

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.R
import com.example.easyteeth.model.Appointment
import com.example.easyteeth.model.Patient
import com.example.easyteeth.model.Box
import navigation.Routes
import viewmodel.AppointmentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
    viewModel: AppointmentViewModel = viewModel()
) {
    val view = LocalView.current
    val datePickerState = rememberDatePickerState()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            viewModel.fetchAppointments(it)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendario", fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TARJETA DEL CALENDARIO (Tal cual estaba antes)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false,
                    title = null,
                    headline = null,
                    colors = DatePickerDefaults.colors(
                        selectedDayContainerColor = Color(0xFF323232),
                        todayDateBorderColor = Color(0xFF1E70EB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Visitas programadas",
                modifier = Modifier.fillMaxWidth(),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // 2. LISTADO DE CITAS (Scrollable)
            // Usamos weight(1f) para que la lista use el espacio sobrante
            Box(modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else if (viewModel.appointmentsByDate.isEmpty()) {
                    Text(
                        "No hay citas para este día",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(viewModel.appointmentsByDate) { appointment ->
                            AppointmentRow(appointment)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. BOTONES INFERIORES
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { navController.navigate(Routes.PATIENTS_APPOINTMENT) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7499D1))
                ) {
                    Text("Agendar cita", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        // Asegúrate de añadir esta ruta en tu NavHost
                        navController.navigate("all_appointments_route")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF7499D1))
                ) {
                    Text("Ver todas las visitas", color = Color(0xFF7499D1), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AppointmentRow(appointment: Appointment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F4F9))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. COLUMNA DE LA HORA Y BOX
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 16.dp)
            ) {
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
                    color = Color(0xFF7499D1),
                    fontWeight = FontWeight.Bold
                )
            }

            Divider(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp),
                color = Color.LightGray
            )

            // 2. INFORMACIÓN DEL PACIENTE Y TRATAMIENTO
            // Dentro de la segunda columna de AppointmentRow
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                val fullname = appointment.patient?.let {
                    "${it.name} ${it.lastname1}"
                } ?: "Paciente desconocido"

                Text(
                    text = fullname,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Acceso directo al nombre del tratamiento
                val tratamientoTexto = appointment.treatment?.name ?: "Tratamiento no asignado"

                Text(
                    text = tratamientoTexto,
                    fontSize = 14.sp,
                    color = Color(0xFF1E70EB), // Azul para resaltar el tratamiento
                    fontWeight = FontWeight.Medium
                )
            }

            // 3. INDICADOR VISUAL
            Surface(
                modifier = Modifier.size(12.dp),
                shape = RoundedCornerShape(50),
                color = Color(0xFF90E0D0)
            ) {}
        }
    }
}
