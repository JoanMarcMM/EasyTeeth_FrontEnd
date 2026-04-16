package com.example.easyteeth.model

import com.google.gson.annotations.SerializedName

data class OrderItemResponse(
    val id: Long = 0L,
    @SerializedName("order_id")
    val order_id: Long = 0L,
    @SerializedName("utensil_id")
    val utensil_id: Long = 0L,
    val quantity: Int = 0,
    @SerializedName("unitPrice")
    val unitPrice: Double = 0.0,
    val utensil: Utensil? = null
)
