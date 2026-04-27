package com.example.easyteeth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.Utensil
import navigation.Routes
import viewmodel.UtensilOrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtensilOrderSelectionScreen(navController: NavController, storageId: Long) {
    val viewModel: UtensilOrderViewModel = viewModel()
    val stockItems by viewModel.stockItems.collectAsState()
    val selectedQuantities by viewModel.selectedQuantities.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(storageId) {
        viewModel.loadStockForStorage(storageId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comanda de Material", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1B4B7C))
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF1E70EB)
                        )
                    }
                    error != null -> {
                        Text(
                            "Error: $error",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    stockItems.isEmpty() -> {
                        Text(
                            "No utensils available",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 100.dp)
                        ) {
                            items(stockItems) { stock ->
                                UtensilOrderCard(
                                    stock = stock,
                                    quantity = selectedQuantities[stock.utensil.id] ?: 0,
                                    onIncrement = { viewModel.incrementQuantity(stock.utensil.id!!) },
                                    onDecrement = { viewModel.decrementQuantity(stock.utensil.id!!) }
                                )
                            }
                        }
                    }
                }
            }

            // Bottom button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Divider()
                Text(
                    "Selected items: ${selectedQuantities.values.sum()}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Button(
                    onClick = {
                        if (selectedQuantities.isNotEmpty()) {
                            navController.navigate(
                                Routes.ORDER_REVIEW.replace("{storageId}", storageId.toString())
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = selectedQuantities.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1B4B7C),
                        disabledContainerColor = Color.LightGray
                    )
                ) {
                    Text(
                        "Seguir amb la comanda",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun UtensilOrderCard(
    stock: com.example.easyteeth.model.StockStorage,
    quantity: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val utensil = stock.utensil
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top row: Name and Stock Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = utensil.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${utensil.brand} / ${utensil.model}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "${stock.quantity} in stock",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF388E3C)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom row: Price and Quantity controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "€${String.format("%.2f", utensil.price)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E70EB)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp),
                        enabled = quantity > 0
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(18.dp),
                            tint = if (quantity > 0) Color(0xFF1E70EB) else Color.LightGray
                        )
                    }

                    Text(
                        text = quantity.toString(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E70EB),
                        modifier = Modifier.width(20.dp),
                        textAlign = TextAlign.Center
                    )

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(18.dp),
                            tint = Color(0xFF1E70EB)
                        )
                    }
                }
            }
        }
    }
}
