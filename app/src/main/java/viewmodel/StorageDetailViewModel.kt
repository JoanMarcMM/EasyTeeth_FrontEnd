package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.StockStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StorageDetailViewModel : ViewModel() {
    private val _stockItems = MutableStateFlow<List<StockStorage>>(emptyList())
    val stockItems: StateFlow<List<StockStorage>> = _stockItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadStockForStorage(storageId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.stockStorageApi.getStockByStorage(storageId)
                if (response.isSuccessful) {
                    _stockItems.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load stock"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
