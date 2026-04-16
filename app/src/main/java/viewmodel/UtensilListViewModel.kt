package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.Utensil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UtensilListViewModel : ViewModel() {
    private val _utensils = MutableStateFlow<List<Utensil>>(emptyList())
    val utensils: StateFlow<List<Utensil>> = _utensils

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadUtensils() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.utensilApi.getAll()
                if (response.isSuccessful) {
                    _utensils.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load utensils"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    init {
        loadUtensils()
    }
}
