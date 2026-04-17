package com.example.easyteeth.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.*
import com.example.easyteeth.api.PatientApiEndpoints
import kotlinx.coroutines.launch
import com.example.easyteeth.model.Patient

class PatientSelectorViewModel : ViewModel() {
    private val api = RetrofitClient.instance.create(PatientApiEndpoints::class.java)

    private var allPatients = listOf<Patient>()
    var filteredPatients = mutableStateListOf<Patient>()

    var isLoading by mutableStateOf(false)
    var searchQuery by mutableStateOf("")

    init {
        fetchPatients()
    }

    fun fetchPatients() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.getAllPatients()
                if (response.isSuccessful) {
                    allPatients = response.body() ?: emptyList()
                    applyFilter("") // Inicializa la lista
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
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