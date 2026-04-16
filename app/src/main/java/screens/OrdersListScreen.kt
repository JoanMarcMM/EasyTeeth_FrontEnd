package screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.UtensilOrderResponse
import viewmodel.OrdersListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersListScreen(navController: NavController) {
    val viewModel: OrdersListViewModel = viewModel()
    val filteredOrders by viewModel.filteredOrders.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val arrivalFilter by viewModel.arrivalFilter.collectAsState()
    val sortBy by viewModel.sortBy.collectAsState()
    val selectedStorageFilter by viewModel.selectedStorageFilter.collectAsState()
    val availableStorages by viewModel.availableStorages.collectAsState()

    var showFiltersBottomSheet by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadAllOrders()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Comandes", fontWeight = FontWeight.Bold) },
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
        ) {
            // Filter pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Arrival filter chips
                FilterChip(
                    selected = arrivalFilter == "all",
                    onClick = { viewModel.setArrivalFilter("all") },
                    label = { Text("Totes", fontSize = 11.sp) },
                    modifier = Modifier.height(32.dp)
                )

                FilterChip(
                    selected = arrivalFilter == "not_arrived",
                    onClick = { viewModel.setArrivalFilter("not_arrived") },
                    label = { Text("Pendent", fontSize = 11.sp) },
                    modifier = Modifier.height(32.dp)
                )

                FilterChip(
                    selected = arrivalFilter == "arrived",
                    onClick = { viewModel.setArrivalFilter("arrived") },
                    label = { Text("Aribades", fontSize = 11.sp) },
                    modifier = Modifier.height(32.dp)
                )

                // Sort chips
                FilterChip(
                    selected = sortBy == "date",
                    onClick = { viewModel.setSortBy("date") },
                    label = { Text("Més recent", fontSize = 11.sp) },
                    modifier = Modifier.height(32.dp)
                )

                FilterChip(
                    selected = sortBy == "date_asc",
                    onClick = { viewModel.setSortBy("date_asc") },
                    label = { Text("Més antiga", fontSize = 11.sp) },
                    modifier = Modifier.height(32.dp)
                )
            }

            // Storage filter dropdown
            if (availableStorages.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Magatzem:", fontSize = 12.sp, color = Color.Gray)
                    
                    var expandedStorage by remember { mutableStateOf(false) }
                    
                    Box {
                        OutlinedButton(
                            onClick = { expandedStorage = true },
                            modifier = Modifier.height(32.dp),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp)
                        ) {
                            Text(
                                selectedStorageFilter?.toString() ?: "Totes",
                                fontSize = 11.sp
                            )
                        }

                        DropdownMenu(
                            expanded = expandedStorage,
                            onDismissRequest = { expandedStorage = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Totes") },
                                onClick = {
                                    viewModel.setStorageFilter(null)
                                    expandedStorage = false
                                }
                            )
                            
                            availableStorages.forEach { storageId ->
                                DropdownMenuItem(
                                    text = { Text("Storage $storageId") },
                                    onClick = {
                                        viewModel.setStorageFilter(storageId)
                                        expandedStorage = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Divider()

            // Orders list
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF1E70EB))
                    }
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "Error",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            error ?: "Unknown error",
                            color = Color(0xFFD32F2F),
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { viewModel.loadAllOrders() },
                            modifier = Modifier.height(40.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Reintentar", fontSize = 12.sp)
                        }
                    }
                }
                filteredOrders.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hi ha comandes", color = Color.Gray, fontSize = 14.sp)
                    }
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        items(filteredOrders) { order ->
                            OrderCard(order, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: UtensilOrderResponse, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(navigation.Routes.ORDER_DETAIL.replace("{orderId}", order.id.toString()))
            },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (order.arrived) Color(0xFFF0F8E8) else Color(0xFFF8F9FA)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Comanda #${order.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Data: ${order.orderDate}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Magatzem: ${order.storage?.numStorage ?: "N/A"}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                // Arrival status badge
                Surface(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = if (order.arrived) Color(0xFF388E3C) else Color(0xFFFFA500)
                ) {
                    Text(
                        text = if (order.arrived) "Aribada" else "Pendent",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Items
            Text(
                text = "${order.orderItems.size} ${if (order.orderItems.size == 1) "article" else "articles"}",
                fontSize = 10.sp,
                color = Color.Gray
            )

            if (order.orderItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                
                order.orderItems.forEachIndexed { index, item ->
                    if (index < 3) { // Show first 3 items
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.utensil?.name ?: "Unknown",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    maxLines = 1
                                )
                            }
                            
                            Text(
                                text = "x${item.quantity}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E70EB)
                            )
                        }
                    }
                }

                if (order.orderItems.size > 3) {
                    Text(
                        text = "+${order.orderItems.size - 3} més",
                        fontSize = 9.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                val total = order.orderItems.sumOf { it.unitPrice * it.quantity }
                Text(
                    text = "Total: €${String.format("%.2f", total)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E70EB)
                )
            }
        }
    }
}
