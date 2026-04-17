package com.example.easyteeth.model

data class StockReductionRequest(
    val boxId: Long,
    val date: String,
    val items: List<ItemReductionRequest>
)

data class ItemReductionRequest(
    val utensilId: Long,
    val quantity: Int
)
