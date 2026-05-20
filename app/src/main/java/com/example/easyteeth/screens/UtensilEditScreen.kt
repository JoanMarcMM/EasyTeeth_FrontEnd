package com.example.easyteeth.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.model.UtensilRequest
import viewmodel.UtensilManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UtensilEditScreen(
    utensilId: Long,
    navController: NavController,
    viewModel: UtensilManagementViewModel = viewModel()
) {
    val utensil = viewModel.selectedUtensil.collectAsState().value
    val suppliers = viewModel.suppliers.collectAsState().value
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    val successMessage = viewModel.successMessage.collectAsState().value

    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedSupplierId by remember { mutableStateOf<Long?>(null) }
    var expandedSupplier by remember { mutableStateOf(false) }

    LaunchedEffect(utensilId) {
        viewModel.loadUtensilById(utensilId)
    }

    LaunchedEffect(utensil) {
        utensil?.let {
            brand = it.brand
            model = it.model
            price = it.price.toString()
            selectedSupplierId = it.supplier?.id
        }
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            navController.navigateUp()
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = { Text("Editar estri", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Enrere", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1B4B7C))
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                isLoading && utensil == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null && utensil == null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error,
                            color = Color.Red,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                utensil != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Display name (non-editable)
                        Text(
                            text = "Nom de l'estri",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        Card(
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFF5F5F5)
                            )
                        ) {
                            Text(
                                text = utensil.name,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        // Brand field (editable)
                        Text(
                            text = "Marca",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = brand,
                            onValueChange = { brand = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("Introduïr marca") }
                        )

                        // Model field (editable)
                        Text(
                            text = "Tipus",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = model,
                            onValueChange = { model = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("Introduïr model") }
                        )

                        // Price field (editable)
                        Text(
                            text = "Preu (€)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        OutlinedTextField(
                            value = price,
                            onValueChange = { price = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            placeholder = { Text("Introduïr preu") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )

                        // Supplier (editable dropdown)
                        Text(
                            text = "Proveïdor *",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedSupplier,
                            onExpandedChange = { expandedSupplier = it }
                        ) {
                            OutlinedTextField(
                                value = suppliers.find { it.id == selectedSupplierId }?.name ?: "Seleccionar proveïdor",
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                shape = RoundedCornerShape(8.dp),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSupplier) }
                            )
                            ExposedDropdownMenu(
                                expanded = expandedSupplier,
                                onDismissRequest = { expandedSupplier = false }
                            ) {
                                suppliers.forEach { supplier ->
                                    DropdownMenuItem(
                                        text = { Text(supplier.name) },
                                        onClick = {
                                            selectedSupplierId = supplier.id
                                            expandedSupplier = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Error message
                        if (error != null) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFFFFEBEE)
                                )
                            ) {
                                Text(
                                    text = error,
                                    color = Color(0xFFC62828),
                                    fontSize = 14.sp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Buttons
                        Button(
                            onClick = {
                                if (brand.isNotBlank() && model.isNotBlank() && price.isNotBlank() && selectedSupplierId != null) {
                                    val priceDouble = price.toDoubleOrNull()
                                    if (priceDouble != null) {
                                        val request = UtensilRequest(
                                            name = utensil.name,
                                            brand = brand,
                                            model = model,
                                            price = priceDouble,
                                            supplierId = selectedSupplierId!!
                                        )
                                        viewModel.updateUtensil(utensil.id!!, request)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            enabled = !isLoading && brand.isNotBlank() && model.isNotBlank() && price.isNotBlank() && selectedSupplierId != null,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                        ) {
                            Text("Guardar canvis")
                        }
                    }
                }
            }
        }
    }
}
