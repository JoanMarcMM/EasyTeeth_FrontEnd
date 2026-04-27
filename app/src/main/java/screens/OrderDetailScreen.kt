package screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.OrderItemResponse
import com.example.easyteeth.model.UtensilOrderResponse
import kotlinx.coroutines.launch
import viewmodel.OrdersListViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(navController: NavController, orderId: Long) {
    val viewModel: OrdersListViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val allOrders by viewModel.allOrders.collectAsState()

    var isMarkingArrived by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Load all orders when screen is composed
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.loadAllOrders()
    }

    // Find the order by ID from allOrders
    val order = allOrders.firstOrNull { it.id == orderId }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Detalls de la Comanda", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xFF1B4B7C))
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        if (order == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Comanda no trobada", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                // Order Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (order.arrived) Color(0xFFF0F8E8) else Color(0xFFF8F9FA)
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Comanda #${order.id}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "Data: ${order.orderDate}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = "Magatzem: ${order.storage?.numStorage ?: "N/A"}",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (order.arrived) Color(0xFF388E3C) else Color(0xFFFFA500)
                            ) {
                                Text(
                                    text = if (order.arrived) "Aribada" else "Pendent",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Text(
                    "Articles encarregats",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
                )

                // Articles List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    items(order.orderItems) { item ->
                        OrderItemDetailCard(item)
                    }
                }

                // Success/Error messages
                if (successMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0F8E8),
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            successMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFF388E3C),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFEBEE),
                        tonalElevation = 1.dp
                    ) {
                        Text(
                            errorMessage ?: "",
                            fontSize = 12.sp,
                            color = Color(0xFFD32F2F),
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Mark as Arrived Button
                if (!order.arrived) {
                    Button(
                        onClick = {
                            isMarkingArrived = true
                            scope.launch {
                                val result = viewModel.markOrderArrived(orderId)
                                result.onSuccess {
                                    successMessage = "Comanda marcada com a aribada"
                                    isMarkingArrived = false
                                    // Navigate back after a short delay
                                    kotlinx.coroutines.delay(1000)
                                    navController.popBackStack()
                                }
                                result.onFailure { exception ->
                                    errorMessage = exception.message ?: "Error unknown"
                                    isMarkingArrived = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF388E3C)
                        ),
                        enabled = !isMarkingArrived
                    ) {
                        if (isMarkingArrived) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Marcar com a Arrivat",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemDetailCard(item: OrderItemResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.utensil?.name ?: "Unknown",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "${item.utensil?.brand ?: "N/A"} / ${item.utensil?.model ?: "N/A"}",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "x${item.quantity}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E70EB)
                    )
                    Text(
                        text = "€${String.format("%.2f", item.unitPrice * item.quantity)}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E70EB)
                    )
                }
            }
        }
    }
}
