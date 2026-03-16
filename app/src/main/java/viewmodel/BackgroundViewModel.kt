package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyteeth.api.BackgroundApiEndpoints
import com.example.easyteeth.model.Background
import com.example.easyteeth.model.BackgroundRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BackgroundViewModel(
    private val api: BackgroundApiEndpoints
) : ViewModel() {

    private val _background = MutableStateFlow<Background?>(null)
    val background: StateFlow<Background?> = _background

    private val _backgrounds = MutableStateFlow<List<Background>>(emptyList())
    val backgrounds: StateFlow<List<Background>> = _backgrounds

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    fun createBackground(request: BackgroundRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.createBackground(request)
                if (response.isSuccessful) {
                    _background.value = response.body()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun getBackgroundById(id: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.getBackgroundById(id)
                if (response.isSuccessful) {
                    _background.value = response.body()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun getBackgroundsByPatientId(patientId: Long) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.getBackgroundsByPatientId(patientId)
                if (response.isSuccessful) {
                    _backgrounds.value = response.body() ?: emptyList()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateBackground(id: Long, request: BackgroundRequest) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.updateBackground(id, request)
                if (response.isSuccessful) {
                    _background.value = response.body()
                }
            } finally {
                _loading.value = false
            }
        }
    }

    fun deleteBackground(id: Long) {
        viewModelScope.launch {
            api.deleteBackground(id)
        }
    }
}