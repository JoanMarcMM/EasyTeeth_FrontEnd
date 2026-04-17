package com.example.easyteeth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.UtensilOrder
import kotlinx.coroutines.launch
import navigation.Routes
import viewmodel.UtensilOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderReviewScreen(navController: NavController, storageId: Long) {
    val viewModel: UtensilOrderViewModel = viewModel()
    val selectedQuantities by viewModel.selectedQuantities.collectAsState()
    val globalSelectedQuantities by UtensilOrderViewModel.getGlobalSelectedQuantities().collectAsState()
    val globalStockItems by UtensilOrderViewModel.getGlobalStockItems().collectAsState()
    val scope = rememberCoroutineScope()
    
    var orderCreated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val selectedOrders = viewModel.getSelectedOrders()
    val totalPrice = selectedOrders.sumOf { it.utensil.price * it.quantity }
    val totalItems = if (globalSelectedQuantities.isNotEmpty()) {
        globalSelectedQuantities.values.sum()
    } else {
        selectedQuantities.values.sum()
    }

    if (orderCreated) {
        // Success screen
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Comanda Confirmada", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
                )
            },
            containerColor = Color.White
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 24.dp),
                    tint = Color(0xFF388E3C)
                )

                Text(
                    "Comanda creada correctament!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                Button(
                    onClick = {
                        navController.navigate(Routes.STORAGE_DETAIL.replace("{storageId}", storageId.toString())) {
                            popUpTo(Routes.STORAGE_DETAIL) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E70EB))
                ) {
                    Text(
                        "Tornar al magatzem",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    } else {
        // Review screen
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Revisió de la Comanda", fontWeight = FontWeight.Bold) },
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
            ) {
                if (selectedOrders.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No items selected", color = Color.Gray)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            items(selectedOrders) { order ->
                                OrderItemSmallCard(order)
                            }
                        }
                    }

                    // Summary and Confirm Button
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (errorMessage != null) {
                            Text(
                                "Error: $errorMessage",
                                fontSize = 12.sp,
                                color = Color(0xFFD32F2F),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }

                        Divider()

                        // Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    "Total items:",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    "Total price:",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    "$totalItems",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    "€${String.format("%.2f", totalPrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E70EB)
                                )
                            }
                        }

                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    val result = viewModel.submitOrder(storageId)
                                    result.onSuccess {
                                        orderCreated = true
                                        isLoading = false
                                    }
                                    result.onFailure { exception ->
                                        errorMessage = exception.message
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(55.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF1E70EB)
                            ),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    "Confirmar comanda",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemSmallCard(order: com.example.easyteeth.model.UtensilOrder) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.utensil.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = "${order.utensil.brand} / ${order.utensil.model}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "x${order.quantity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E70EB)
                    )
                    Text(
                        text = "€${String.format("%.2f", order.utensil.price * order.quantity)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E70EB)
                    )
                }
            }
        }
    }
}

