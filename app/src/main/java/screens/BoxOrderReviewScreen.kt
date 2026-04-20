package screens

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
import com.example.easyteeth.model.OrderItemRequest
import com.example.easyteeth.model.StockBox
import com.example.easyteeth.model.UtensilOrderRequest
import kotlinx.coroutines.launch
import api.RetrofitClient
import viewmodel.BoxViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxOrderReviewScreen(
    navController: NavController,
    boxId: Long,
    dateMillis: Long
) {
    val viewModel: BoxViewModel = viewModel()
    val scope = rememberCoroutineScope()
    
    val globalBoxMaterials by BoxViewModel.getGlobalBoxMaterials().collectAsState()
    val globalOrderDate by BoxViewModel.getGlobalOrderDate().collectAsState()
    
    var orderCreated by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val totalPrice = globalBoxMaterials.sumOf { material ->
        (material.utensil.price) * material.quantity
    }
    val totalItems = globalBoxMaterials.sumOf { it.quantity }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Revisar Comanda", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            BoxViewModel.clearGlobalBoxOrder()
                            navController.popBackStack()
                        }
                    ) {
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (globalBoxMaterials.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No materials found", color = Color.Gray, fontSize = 16.sp)
                }
            } else {
                // Header with summary
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Resum de la comanda",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total d'articles:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = totalItems.toString(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Preu total:", fontSize = 12.sp, color = Color.Gray)
                            Text(
                                text = String.format("€%.2f", totalPrice),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E70EB)
                            )
                        }
                    }
                }

                // Materials List
                Text(
                    text = "Instrumental a encarregar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(globalBoxMaterials) { material ->
                        BoxOrderItemCard(material)
                    }
                }
            }

            // Success/Error Messages
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
                        color = Color(0xFF2E7D32),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (errorMessage != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFCE4EC),
                    tonalElevation = 1.dp
                ) {
                    Text(
                        errorMessage ?: "",
                        fontSize = 12.sp,
                        color = Color(0xFFC2185B),
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Confirm Button
            Button(
                enabled = globalBoxMaterials.isNotEmpty() && !isLoading && !orderCreated,
                onClick = {
                    isLoading = true
                    errorMessage = null
                    successMessage = null

                    scope.launch {
                        try {
                            // Get all storages to find the first one
                            val storagesResponse = RetrofitClient.storageApi.getAllStorages()
                            if (!storagesResponse.isSuccessful || storagesResponse.body() == null) {
                                errorMessage = "Error obteniendo los almacenes"
                                isLoading = false
                                return@launch
                            }

                            val storages = storagesResponse.body()!!
                            if (storages.isEmpty()) {
                                errorMessage = "No hay almacenes disponibles"
                                isLoading = false
                                return@launch
                            }

                            // Use the first storage
                            val storage = storages.first()

                            // Create order items from materials
                            val orderItems = globalBoxMaterials.map { material ->
                                OrderItemRequest(
                                    utensilId = material.utensil.id ?: 0,
                                    quantity = material.quantity,
                                    unitPrice = material.utensil.price
                                )
                            }

                            // Create the order request
                            val orderRequest = UtensilOrderRequest(
                                orderDate = globalOrderDate,
                                storageId = storage.id ?: 0,
                                orderItems = orderItems
                            )

                            // Send the order to the API
                            val response = RetrofitClient.utensilOrderApi.createOrder(orderRequest)
                            
                            if (response.isSuccessful) {
                                successMessage = "Comanda creada correctament!"
                                orderCreated = true
                                isLoading = false
                                
                                // Navigate back after a short delay
                                kotlinx.coroutines.delay(1500)
                                BoxViewModel.clearGlobalBoxOrder()
                                navController.popBackStack()
                            } else {
                                errorMessage = "Error al crear la comanda: ${response.code()}"
                                isLoading = false
                            }
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 16.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E70EB),
                    disabledContainerColor = Color(0xFFBDBDBD)
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Confirmar",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Confirmar Comanda",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BoxOrderItemCard(material: StockBox) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = material.utensil.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Text(
                    text = "${material.utensil.brand} / ${material.utensil.model}",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    maxLines = 1
                )
                Text(
                    text = "€${String.format("%.2f", material.utensil.price)}",
                    fontSize = 11.sp,
                    color = Color(0xFF1E70EB),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = "x${material.quantity}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "€${String.format("%.2f", material.utensil.price * material.quantity)}",
                    fontSize = 11.sp,
                    color = Color(0xFF1E70EB),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
