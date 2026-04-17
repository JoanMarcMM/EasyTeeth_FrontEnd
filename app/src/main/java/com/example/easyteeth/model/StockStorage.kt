package com.example.easyteeth.model

data class StockStorage(
    val id: Long? = null,
    val utensil: Utensil,
    val storage: Storage,
    val quantity: Int
)
