package com.example.easyteeth.model

data class Utensil(
    val id: Long? = null,
    val name: String,
    val brand: String,
    val model: String,
    val price: Double,
    val supplier: Supplier? = null
)
