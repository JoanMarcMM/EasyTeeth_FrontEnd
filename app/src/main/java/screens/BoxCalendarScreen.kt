package screens

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.WarningAmber
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
import navigation.Routes
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
    var orderCreationInProgress by remember { mutableStateOf(false) }

    val materials by viewModel.materials.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val hasInsufficientStock by viewModel.hasInsufficientStock.collectAsState()
    val insufficientUtensilsList by viewModel.insufficientUtensilsList.collectAsState()
    val orderCreating by viewModel.orderCreating.collectAsState()
    val orderError by viewModel.orderError.collectAsState()
    val orderSuccess by viewModel.orderSuccess.collectAsState()

    // Check if selected date is today
    val isToday = remember(datePickerState.selectedDateMillis) {
        val calendar = Calendar.getInstance()
        val selectedCalendar = Calendar.getInstance()
        selectedCalendar.timeInMillis = datePickerState.selectedDateMillis ?: todayMillis
        
        calendar.get(Calendar.YEAR) == selectedCalendar.get(Calendar.YEAR) &&
        calendar.get(Calendar.DAY_OF_YEAR) == selectedCalendar.get(Calendar.DAY_OF_YEAR)
    }

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    // Fetch materials whenever the selected date changes
    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let { millis ->
            showSuccessMessage = false
            isError = false
            viewModel.clearOrderMessages()
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

            // Show date warning - not today
            if (!isToday && materials.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3E5F5))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Info",
                                tint = Color(0xFF6A1B9A),
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "You are viewing materials for a future date. You can create an order with these exact items.",
                                color = Color(0xFF4A148C),
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (orderError != null) {
                            Text(
                                text = "Error: $orderError",
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        if (orderSuccess) {
                            Text(
                                text = "Order created successfully!",
                                color = Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            // Show insufficient stock warning
            if (hasInsufficientStock && materials.isNotEmpty() && isToday) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Row(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Advertencia",
                            tint = Color(0xFFC62828),
                            modifier = Modifier.size(20.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "No hay suficientes utensiles disponibles",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFC62828),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            if (insufficientUtensilsList.isNotEmpty()) {
                                Text(
                                    text = "Falta:",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFB71C1C),
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                insufficientUtensilsList.forEach { item ->
                                    Text(
                                        text = "• $item",
                                        color = Color(0xFFB71C1C),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Button(
                enabled = datePickerState.selectedDateMillis != null && 
                        materials.isNotEmpty() && 
                        (isToday && !hasInsufficientStock || !isToday) &&
                        !orderCreating,
                onClick = {
                    if (isToday) {
                        // Today: confirm/cancel restock
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
                    } else {
                        // Future date: navigate to order review screen
                        datePickerState.selectedDateMillis?.let { millis ->
                            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(millis))
                            viewModel.setGlobalBoxOrderForReview(boxId, dateStr, materials)
                            navController.navigate("${Routes.BOX_ORDER_REVIEW}".replace("{boxId}", boxId.toString()).replace("{dateMillis}", millis.toString()))
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
                    containerColor = if (isToday) {
                        if (isAllStocked) Color(0xFFD32F2F) else Color(0xFF65558F)
                    } else {
                        Color(0xFF1E70EB)
                    },
                    disabledContainerColor = Color(0xFFBDBDBD)
                )
            ) {
                if (orderCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isToday) {
                            if (isAllStocked) "Cancel·lar Reposció" else "Confirmar Reposció"
                        } else {
                            "Crear Comanda"
                        },
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
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