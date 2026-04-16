package com.example.easyteeth.model

data class OrderItemRequest(
    val utensilId: Long,
    val quantity: Int,
    val unitPrice: Double? = null
)

data class UtensilOrderRequest(
    val orderDate: String,
    val storageId: Long,
    val orderItems: List<OrderItemRequest>
)
