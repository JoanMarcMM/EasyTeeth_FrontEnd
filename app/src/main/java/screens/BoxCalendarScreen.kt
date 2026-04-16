package com.example.easyteeth.screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.example.easyteeth.model.StockBox
import viewmodel.BoxViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxCalendarScreen(
    navController: NavController,
    boxId: Long,
    numBox: Int,
    viewModel: BoxViewModel
) {
    val view = LocalView.current

    val todayMillis = remember { System.currentTimeMillis() }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = todayMillis)

    var showSuccessMessage by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.loading.collectAsState()

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        showSuccessMessage = false
        isError = false

        datePickerState.selectedDateMillis?.let { millis ->
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
            viewModel.fetchMaterials(boxId, dateStr)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Programar Box $numBox", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
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
            Text(
                text = "Selecciona el dia per preparar els materials del Box $numBox:",
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                fontSize = 16.sp,
                color = Color.DarkGray
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
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
                        selectedDayContainerColor = Color(0xFF1E70EB),
                        todayDateBorderColor = Color(0xFF1E70EB)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (datePickerState.selectedDateMillis != null) {
                if (isLoading) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF65558F))
                    }
                } else if (materials.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text("No hi ha materials programats", color = Color.Gray)
                    }
                } else {
                    Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        Text(
                            text = "Instrumental necessari",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(materials) { item ->
                                MaterialItemRow(item)
                            }
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            val isAllStocked = materials.isNotEmpty() && materials.all { it.stocked }

            if (showSuccessMessage) {
                Text(
                    text = if (isAllStocked) "Materials reposats correctament!" else "Reposició cancel·lada!",
                    color = if (isAllStocked) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            if (isError) {
                Text(
                    text = "Error en connectar amb el servidor",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                enabled = datePickerState.selectedDateMillis != null && materials.isNotEmpty(),
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val newStatus = !isAllStocked

                        viewModel.updateStockStatus(boxId, millis, newStatus) { success ->
                            if (success) {
                                showSuccessMessage = true
                                isError = false
                                val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                                viewModel.fetchMaterials(boxId, dateStr)
                            } else {
                                isError = true
                                showSuccessMessage = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAllStocked) Color(0xFFD32F2F) else Color(0xFF65558F)
                )
            ) {
                Text(
                    text = if (isAllStocked) "Cancel·lar Reposició" else "Confirmar Reposició",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun MaterialItemRow(item: StockBox) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(if (item.stocked) Color(0xFF4CAF50) else Color.Red, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = item.utensil.name, fontWeight = FontWeight.Medium)
            }
            Text(text = "x${item.quantity}", fontWeight = FontWeight.Bold)
        }
    }
}