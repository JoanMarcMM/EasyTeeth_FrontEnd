package com.example.easyteeth.model

data class Patient(
    val id: Long? = null,
    val name: String,
    val lastname1: String,
    val lastname2: String,
    val dni: String,
    val ssn: String,
    val phoneNumber: String? = null,
    val email: String? = null,
    val billingAddress: String? = null,
    val bankAccountNumber: String? = null,
    val taxIdentificationNumber: String? = null,
    val isContagious: Boolean = false,
    val hasAllergies: Boolean = false
)