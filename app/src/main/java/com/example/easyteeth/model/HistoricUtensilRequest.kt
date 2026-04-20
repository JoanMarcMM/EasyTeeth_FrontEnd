package com.example.easyteeth.model

data class HistoricUtensilRequest(
    val name: String,
    val brand: String,
    val model: String,
    val price: Double,
    val supplierId: Long
)
