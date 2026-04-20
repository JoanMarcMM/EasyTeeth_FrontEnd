package com.example.easyteeth.model

import java.time.LocalDateTime

data class HistoricUtensil(
    val id: Long? = null,
    val name: String,
    val brand: String,
    val model: String,
    val price: Double,
    val supplier: Supplier? = null,
    val dateAdded: LocalDateTime? = null
)
