package com.example.easyteeth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.StockStorage
import navigation.Routes
import viewmodel.StorageDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StorageDetailScreen(navController: NavController, storageId: Long) {
    val viewModel: StorageDetailViewModel = viewModel()
    val stockItems by viewModel.stockItems.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(storageId) {
        viewModel.loadStockForStorage(storageId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Magatzem #$storageId - Stock", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1B4B7C))
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(Routes.UTENSIL_ORDER_SELECTION.replace("{storageId}", storageId.toString())) },
                containerColor = Color(0xFF1B4B7C),
                contentColor = Color.White,
                shape = RoundedCornerShape(50),

            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear orden")
            }
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxSize()) {
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
                            "No stock items",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(bottom = 20.dp)
                        ) {
                            items(stockItems) { stock ->
                                StockItemCard(stock)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StockItemCard(stock: StockStorage) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Utensil name
            Text(
                text = stock.utensil.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Brand and Model row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Brand",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stock.utensil.brand,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Model",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = stock.utensil.model,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price and Quantity row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Price",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "€${String.format("%.2f", stock.utensil.price)}",
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Quantity",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Badge(
                        containerColor = Color(0xFF1E70EB),
                        contentColor = Color.White
                    ) {
                        Text(
                            "${stock.quantity}",
                            modifier = Modifier.padding(4.dp),
                            fontWeight = FontWeight.Bold

                        )
                    }
                }
            }
        }
    }
}
