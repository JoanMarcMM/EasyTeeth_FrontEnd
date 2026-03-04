package model

data class Patient(
    val id: Long? = null,
    val name: String,
    val lastname1: String,
    val lastname2: String,
    val dni: String,
    val ssn: String
)