package com.example.easyteeth.model

data class Background(
    val id: Long? = null,
    val familyHistory: String,
    val healthState: String,
    val lifeHabits: String,
    val allergies: String,
    val medication: String,
    val importantAllergie: Boolean,
    val infectiousDisease: Boolean,
    val hasSignedConsent: Boolean,
    val hasSignedAnesthesia: Boolean,
    val patient: Patient
)