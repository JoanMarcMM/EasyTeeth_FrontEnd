package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.Supplier
import com.example.easyteeth.model.SupplierRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupplierManagementViewModel : ViewModel() {
    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers

    private val _selectedSupplier = MutableStateFlow<Supplier?>(null)
    val selectedSupplier: StateFlow<Supplier?> = _selectedSupplier

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun loadSuppliers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.supplierApi.getAll()
                if (response.isSuccessful) {
                    _suppliers.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Failed to load suppliers"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadSupplierById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.supplierApi.getSupplierById(id)
                if (response.isSuccessful) {
                    _selectedSupplier.value = response.body()
                } else {
                    _error.value = "Failed to load supplier"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSupplier(request: SupplierRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                val response = RetrofitClient.supplierApi.createSupplier(request)
                if (response.isSuccessful) {
                    _successMessage.value = "Supplier created successfully"
                    // Reload list to update it
                    loadSuppliers()
                } else {
                    _error.value = "Failed to create supplier"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateSupplier(id: Long, request: SupplierRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                val response = RetrofitClient.supplierApi.updateSupplier(id, request)
                if (response.isSuccessful) {
                    _successMessage.value = "Supplier updated successfully"
                    _selectedSupplier.value = response.body()
                    // Reload list to update it
                    loadSuppliers()
                } else {
                    _error.value = "Failed to update supplier"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSupplier(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                val response = RetrofitClient.supplierApi.deleteSupplier(id)
                if (response.isSuccessful) {
                    _successMessage.value = "Supplier deleted successfully"
                    _selectedSupplier.value = null
                    // Reload list to update it
                    loadSuppliers()
                } else {
                    _error.value = "Failed to delete supplier"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSuccessMessage() {
        _successMessage.value = null
    }

    init {
        loadSuppliers()
    }
}
