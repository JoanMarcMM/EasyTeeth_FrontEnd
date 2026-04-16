package com.example.easyteeth.model

import com.google.gson.annotations.SerializedName

data class UtensilOrderResponse(
    val id: Long = 0L,
    @SerializedName("orderDate")
    val orderDate: String = "",
    val arrived: Boolean = false,
    @SerializedName("storage_id")
    val storage_id: Long = 0L,
    @SerializedName("orderItems")
    val orderItems: List<OrderItemResponse> = emptyList(),
    val storage: Storage? = null
)
