package screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftSelectionScreen(
    patientId: Long,
    treatmentId: Long,
    odontologistId: Long,
    motive: String,
    hasMedicalAlert: Boolean = false,
    navController: NavController
) {
    val darkBlue = Color(0xFF1B4B7C)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccionar torn",
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
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasMedicalAlert) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Pacient amb advertència mèdica: programat automàticament al final del dia.",
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                "¿En quin horari vols fer la cita?",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Morning Shift Button
            Button(
                onClick = {
                    navController.navigate(
                        Routes.selectBoxes(patientId, treatmentId, odontologistId, motive, "MORNING", hasMedicalAlert)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                enabled = !hasMedicalAlert,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasMedicalAlert) Color.LightGray else Color(0xFFE8F5E9),
                    disabledContainerColor = Color(0xFFF5F5F5)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "MATÍ",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hasMedicalAlert) Color.Gray else Color(0xFF2E7D32)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "08:00 - 12:00",
                        fontSize = 14.sp,
                        color = if (hasMedicalAlert) Color.Gray else Color(0xFF558B2F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Afternoon Shift Button
            Button(
                onClick = {
                    navController.navigate(
                        Routes.selectBoxes(patientId, treatmentId, odontologistId, motive, "AFTERNOON", hasMedicalAlert)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasMedicalAlert) Color(0xFFFFCC80) else Color(0xFFFFF3E0)
                ),
                border = if (hasMedicalAlert) BorderStroke(2.dp, Color(0xFFE65100)) else null
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        "TARDA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "13:00 - 17:00",
                        fontSize = 14.sp,
                        color = Color(0xFFBF360C)
                    )
                }
            }
        }
    }
}
