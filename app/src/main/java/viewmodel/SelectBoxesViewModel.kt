package viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.BoxApiEndpoints
import com.example.easyteeth.model.Box
import kotlinx.coroutines.launch

class SelectBoxesViewModel : ViewModel() {
    private val boxApi = RetrofitClient.instance.create(BoxApiEndpoints::class.java)

    // Datos recibidos de la pantalla anterior
    var patientId by mutableStateOf<Long?>(null)
    var treatmentId by mutableStateOf<Long?>(null)
    var odontologistId by mutableStateOf<Long?>(null)
    var motive by mutableStateOf("")
    var shift by mutableStateOf("MORNING")

    // Estados de carga
    var isLoadingBoxes by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Datos disponibles
    var availableBoxes = mutableStateListOf<Box>()

    // Selecciones del usuario
    var selectedBox by mutableStateOf<Box?>(null)

    fun initialize(patientId: Long, treatmentId: Long, odontologistId: Long, motive: String, shift: String) {
        this.patientId = patientId
        this.treatmentId = treatmentId
        this.odontologistId = odontologistId
        this.motive = motive
        this.shift = shift

        viewModelScope.launch {
            loadBoxes()
        }
    }

    private suspend fun loadBoxes() {
        isLoadingBoxes = true
        errorMessage = null

        try {
            val response = boxApi.getAllBoxes()

            if (!response.isSuccessful) {
                errorMessage = "Error al obtener consultorios: ${response.code()}"
                return
            }

            val boxes = response.body() ?: emptyList()

            availableBoxes.clear()
            availableBoxes.addAll(boxes)

            // Auto-select first box
            if (availableBoxes.isNotEmpty()) {
                selectedBox = availableBoxes[0]
            }

        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            android.util.Log.e("SelectBoxesViewModel", "Error loading boxes", e)
        } finally {
            isLoadingBoxes = false
        }
    }
}
