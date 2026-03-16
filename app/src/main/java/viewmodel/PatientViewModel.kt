package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyteeth.api.PatientApiEndpoints
import com.example.easyteeth.model.Patient
import com.example.easyteeth.model.PatientRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(
    private val api: PatientApiEndpoints
) : ViewModel() {

    private val _createdPatient = MutableStateFlow<Patient?>(null)
    val createdPatient: StateFlow<Patient?> = _createdPatient

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createPatient(request: PatientRequest) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                val response = api.createPatient(request)
                if (response.isSuccessful) {
                    _createdPatient.value = response.body()
                } else {
                    _error.value = "Error al crear el paciente: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearCreatedPatient() {
        _createdPatient.value = null
    }
}