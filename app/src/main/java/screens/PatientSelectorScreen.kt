package com.example.easyteeth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.example.easyteeth.model.Patient
import com.example.easyteeth.viewmodel.PatientSelectorViewModel
import com.example.easyteeth.ui.MedicalAlerts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientSelectorScreen(
    navController: NavController,
    viewModel: PatientSelectorViewModel = viewModel()
) {
    val darkBlue = Color(0xFF1B4B7C)
    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Seleccionar Pacient",
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
                .padding(horizontal = 20.dp)
        ) {
            // BUSCADOR POR NOMBRE O DNI
            OutlinedTextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.applyFilter(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                placeholder = { Text("Buscar per nom o DNI...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.applyFilter("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Text(
                text = "Resultats trobats: ${viewModel.filteredPatients.size}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // LISTADO DE PACIENTES
            Box(modifier = Modifier.weight(1f)) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF1E70EB))
                } else if (viewModel.filteredPatients.isEmpty()) {
                    Text(
                        "No se encontraron pacientes",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(bottom = 20.dp)
                    ) {
                        items(viewModel.filteredPatients) { patient ->
                            PatientCard(patient = patient, viewModel = viewModel) {
                                // Al hacer clic, navegamos a la pantalla de detalles de la cita
                                // pasando el ID del paciente seleccionado
                                patient.id?.let { patientId ->
                                    navController.navigate("add_appointment_details/$patientId")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientCard(patient: Patient, viewModel: PatientSelectorViewModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "${patient.name} ${patient.lastname1} ${patient.lastname2}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    
                    // Unified medical alerts
                    val isContagious = patient.isContagious || viewModel.hasInfectiousDisease(patient.id)
                    val hasAllergies = patient.hasAllergies || viewModel.hasAllergie(patient.id)
                    
                    MedicalAlerts(isContagious = isContagious, hasAllergies = hasAllergies)
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Badge(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF1E70EB)) {
                        Text("DNI: ${patient.dni}", modifier = Modifier.padding(4.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Badge(containerColor = Color(0xFFF1F8E9), contentColor = Color(0xFF388E3C)) {
                        Text("SSN: ${patient.ssn}", modifier = Modifier.padding(4.dp))
                    }
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}