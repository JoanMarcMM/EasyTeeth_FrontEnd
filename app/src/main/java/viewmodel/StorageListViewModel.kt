package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StorageListViewModel : ViewModel() {
    private val _storages = MutableStateFlow<List<Storage>>(emptyList())
    val storages: StateFlow<List<Storage>> = _storages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStorages() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.storageApi.getAllStorages()
                if (response.isSuccessful) {
                    _storages.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load storages"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    init {
        loadStorages()
    }
}
