package com.example.easyteeth.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.*
import com.example.easyteeth.api.BackgroundApiEndpoints
import com.example.easyteeth.api.PatientApiEndpoints
import com.example.easyteeth.model.Background
import kotlinx.coroutines.launch
import com.example.easyteeth.model.Patient

class PatientSelectorViewModel : ViewModel() {
    private val patientApi = RetrofitClient.instance.create(PatientApiEndpoints::class.java)
    private val backgroundApi = RetrofitClient.instance.create(BackgroundApiEndpoints::class.java)

    private var allPatients = listOf<Patient>()
    var filteredPatients = mutableStateListOf<Patient>()
    
    // Map to store background data for each patient - made reactive
    var patientBackgrounds = mutableStateMapOf<Long, Background>()

    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    init {
        fetchPatients()
    }

    fun fetchPatients() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = patientApi.getAllPatients()
                if (response.isSuccessful) {
                    allPatients = response.body() ?: emptyList()
                    applyFilter("") // Inicializa la lista
                    
                    // Fetch background data for each patient
                    fetchBackgroundsForPatients(allPatients)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    private fun fetchBackgroundsForPatients(patients: List<Patient>) {
        viewModelScope.launch {
            for (patient in patients) {
                patient.id?.let { patientId ->
                    try {
                        val response = backgroundApi.getBackgroundsByPatientId(patientId)
                        if (response.isSuccessful) {
                            val backgrounds = response.body() ?: emptyList()
                            if (backgrounds.isNotEmpty()) {
                                // Store the first background for this patient
                                patientBackgrounds[patientId] = backgrounds[0]
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun getBackgroundForPatient(patientId: Long): Background? {
        return patientBackgrounds[patientId]
    }

    fun hasAllergie(patientId: Long?): Boolean {
        return patientId?.let { patientBackgrounds[it]?.importantAllergie ?: false } ?: false
    }

    fun hasInfectiousDisease(patientId: Long?): Boolean {
        return patientId?.let { patientBackgrounds[it]?.infectiousDisease ?: false } ?: false
    }

    fun applyFilter(query: String) {
        searchQuery = query
        val q = query.lowercase().trim()

        val filtered = if (q.isEmpty()) {
            allPatients
        } else {
            allPatients.filter { patient ->
                patient.name.lowercase().contains(q) ||
                        patient.dni.lowercase().contains(q) ||
                        patient.lastname1.lowercase().contains(q)
            }
        }

        // Sort by ID in descending order (newest to oldest)
        val sortedFiltered = filtered.sortedByDescending { it.id }

        filteredPatients.clear()
        filteredPatients.addAll(sortedFiltered)
    }

    // Función corregida para evitar errores de nombres
    private fun updateList(newList: List<Patient>) {
        filteredPatients.clear()
        filteredPatients.addAll(newList)
    }
}