package com.example.easyteeth.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.AppointmentApiEndpoints
import kotlinx.coroutines.launch
import com.example.easyteeth.model.Appointment

class AppointmentSearcherViewModel : ViewModel() {
    private val api = RetrofitClient.instance.create(AppointmentApiEndpoints::class.java)

    private var allAppointments = listOf<Appointment>()
    var filteredAppointments = mutableStateListOf<Appointment>()
    var isLoading by mutableStateOf(false)

    // Estados de filtros
    var filterPatient by mutableStateOf("")
    var filterDate by mutableStateOf("")
    var filterTime by mutableStateOf("")
    var filterTreatment by mutableStateOf("Todos")
    var filterOdontologist by mutableStateOf("Todos")
    var filterBox by mutableStateOf("Todos")

    // Listas para los desplegables (se llenan al cargar las citas)
    var treatmentOptions = mutableStateListOf<String>()
    var odontologistOptions = mutableStateListOf<String>()
    var boxOptions = mutableStateListOf<String>()

    init {
        fetchAllAppointments()
    }

    fun fetchAllAppointments() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.getAllAppointments()
                if (response.isSuccessful) {
                    val body = response.body() ?: emptyList()
                    allAppointments = body

                    // Extraer opciones únicas para los desplegables
                    treatmentOptions.clear()
                    treatmentOptions.add("Todos")
                    treatmentOptions.addAll(body.mapNotNull { it.treatment?.name }.distinct().sorted())

                    odontologistOptions.clear()
                    odontologistOptions.add("Todos")
                    odontologistOptions.addAll(body.mapNotNull { it.odontologist?.let { o -> "${o.name} ${o.lastname1}" } }.distinct().sorted())

                    boxOptions.clear()
                    boxOptions.add("Todos")
                    boxOptions.addAll(body.mapNotNull { it.box?.numBox?.toString() }.distinct().sorted())

                    applyAllFilters()
                }
            } catch (e: Exception) { e.printStackTrace() }
            finally { isLoading = false }
        }
    }

    fun applyAllFilters() {
        val filtered = allAppointments.filter { appointment ->
            val matchPatient = appointment.patient?.let {
                "${it.name} ${it.lastname1}".lowercase().contains(filterPatient.lowercase())
            } ?: true

            val dateParts = appointment.date.split("T")
            val matchDate = if (filterDate.isEmpty()) true else dateParts.getOrNull(0) == filterDate
            val matchTime = if (filterTime.isEmpty()) true else dateParts.getOrNull(1)?.contains(filterTime) ?: true

            val matchTreatment = if (filterTreatment == "Todos") true
            else appointment.treatment?.name == filterTreatment

            // CAMBIO AQUÍ: Filtramos por el nombre del odontólogo
            val matchOdonto = if (filterOdontologist == "Todos") true
            else "${appointment.odontologist?.name} ${appointment.odontologist?.lastname1}" == filterOdontologist

            val matchBox = if (filterBox == "Todos") true
            else appointment.box?.numBox?.toString() == filterBox

            matchPatient && matchDate && matchTime && matchTreatment && matchOdonto && matchBox
        }
        filteredAppointments.clear()
        filteredAppointments.addAll(filtered)
    }

    fun clearFilters() {
        filterPatient = ""; filterDate = ""; filterTime = ""
        filterTreatment = "Todos"; filterOdontologist = "Todos"; filterBox = "Todos"
        applyAllFilters()
    }
}