package com.example.easyteeth.model

data class StockBox(
    val id: Long,
    val quantity: Int,
    val stocked: Boolean,
    val day: String?,
    val utensil: Utensil,
    val box: Box
)