package viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.AppointmentApiEndpoints
import api.OdontologistApiEndpoints
import api.BoxApiEndpoints
import api.TreatmentApiEndpoints
import com.example.easyteeth.model.AppointmentRequest
import com.example.easyteeth.model.Box
import com.example.easyteeth.model.Odontologist
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SelectAppointmentDateTimeViewModel : ViewModel() {
    private val appointmentApi = RetrofitClient.instance.create(AppointmentApiEndpoints::class.java)
    private val treatmentApi = RetrofitClient.instance.create(TreatmentApiEndpoints::class.java)
    private val odontologistApi = RetrofitClient.instance.create(OdontologistApiEndpoints::class.java)
    private val boxApi = RetrofitClient.instance.create(BoxApiEndpoints::class.java)

    // Datos recibidos de la pantalla anterior
    var patientId by mutableStateOf<Long?>(null)
    var treatmentId by mutableStateOf<Long?>(null)
    var motive by mutableStateOf("")

    // Estados de carga
    var isLoadingOdontologists by mutableStateOf(false)
    var isLoadingBoxes by mutableStateOf(false)
    var isCreating by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Datos disponibles
    var availableOdontologists = mutableStateListOf<Odontologist>()
    var availableBoxes = mutableStateListOf<Box>()

    // Selecciones del usuario
    var selectedDateTime by mutableStateOf<Date?>(null)
    var selectedHour by mutableStateOf("")
    var selectedMinute by mutableStateOf("")
    var selectedOdontologist by mutableStateOf<Odontologist?>(null)
    var selectedBox by mutableStateOf<Box?>(null)

    fun initialize(patientId: Long, treatmentId: Long, odontologistId: Long, boxId: Long, motive: String) {
        this.patientId = patientId
        this.treatmentId = treatmentId
        this.motive = motive

        viewModelScope.launch {
            val odontologistsJob = viewModelScope.launch { loadOdontologists() }
            val boxesJob = viewModelScope.launch { loadBoxes() }

            odontologistsJob.join()
            boxesJob.join()
        }
    }

    private suspend fun loadOdontologists() {
        isLoadingOdontologists = true

        try {
            // Primero obtener el tratamiento con sus especialidades
            if (treatmentId == null || treatmentId!! <= 0) {
                errorMessage = "TratamientoId inválido"
                return
            }

            val treatmentResponse = treatmentApi.getTreatment(treatmentId!!)

            if (!treatmentResponse.isSuccessful) {
                errorMessage = "Error al obtener tratamiento: ${treatmentResponse.code()}"
                return
            }

            val treatment = treatmentResponse.body()

            if (treatment == null || treatment.specialities == null || treatment.specialities.isEmpty()) {
                errorMessage = "El tratamiento no tiene especialidades asociadas"
                availableOdontologists.clear()
                return
            }

            // Obtener odontólogos para cada especialidad
            val odontologistsSet = mutableSetOf<Odontologist>()

            for (speciality in treatment.specialities) {
                try {
                    val odontologistResponse = odontologistApi.findBySpeciality(speciality.id)

                    if (odontologistResponse.isSuccessful) {
                        val odontologists = odontologistResponse.body() ?: emptyList()
                        odontologistsSet.addAll(odontologists)
                    }
                } catch (e: Exception) {
                    // Continuar con otras especialidades
                }
            }

            availableOdontologists.clear()
            availableOdontologists.addAll(odontologistsSet.toList())

        } catch (e: Exception) {
            errorMessage = "Error al cargar odontólogos: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingOdontologists = false
        }
    }

    private suspend fun loadBoxes() {
        isLoadingBoxes = true

        try {
            val response = boxApi.getAllBoxes()
            if (response.isSuccessful) {
                val boxes = response.body() ?: emptyList()
                availableBoxes.clear()
                availableBoxes.addAll(boxes)
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar cajas: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingBoxes = false
        }
    }

    fun isReadyToProceed(): Boolean {
        return selectedDateTime != null &&
                selectedHour.isNotEmpty() &&
                selectedMinute.isNotEmpty() &&
                selectedOdontologist != null &&
                selectedBox != null
    }

    fun createAppointment(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isReadyToProceed()) {
            onError("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            isCreating = true
            errorMessage = null

            try {
                // Construir DateTime
                val calendar = Calendar.getInstance().apply {
                    time = selectedDateTime ?: Date()
                    set(Calendar.HOUR_OF_DAY, selectedHour.toIntOrNull() ?: 0)
                    set(Calendar.MINUTE, selectedMinute.toIntOrNull() ?: 0)
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateTimeString = formatter.format(calendar.time)

                // Crear request
                val request = AppointmentRequest(
                    motive = motive,
                    date = dateTimeString,
                    patientId = patientId ?: 0L,
                    boxId = selectedBox?.id ?: 0L,
                    odontologistId = selectedOdontologist?.id ?: 0L,
                    treatmentId = treatmentId ?: 0L
                )

                // Llamar al API
                val response = appointmentApi.createAppointment(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al crear cita: ${response.code()}")
                    errorMessage = "Error al crear cita: ${response.code()}"
                }
            } catch (e: Exception) {
                val errorMsg = "Excepción: ${e.message}"
                onError(errorMsg)
                errorMessage = errorMsg
                e.printStackTrace()
            } finally {
                isCreating = false
            }
        }
    }
}
