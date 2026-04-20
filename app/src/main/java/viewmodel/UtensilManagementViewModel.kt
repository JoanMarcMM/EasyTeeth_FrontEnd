package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.Supplier
import com.example.easyteeth.model.Utensil
import com.example.easyteeth.model.UtensilRequest
import com.example.easyteeth.model.HistoricUtensilRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async

class UtensilManagementViewModel : ViewModel() {
    private val _utensils = MutableStateFlow<List<Utensil>>(emptyList())
    val utensils: StateFlow<List<Utensil>> = _utensils

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers

    private val _selectedUtensil = MutableStateFlow<Utensil?>(null)
    val selectedUtensil: StateFlow<Utensil?> = _selectedUtensil

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

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

    fun loadSuppliers() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.supplierApi.getAll()
                if (response.isSuccessful) {
                    _suppliers.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load suppliers"
            }
        }
    }

    fun loadUtensilById(id: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.utensilApi.getUtensilById(id)
                if (response.isSuccessful) {
                    _selectedUtensil.value = response.body()
                } else {
                    _error.value = "Failed to load utensil"
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUtensil(id: Long, request: UtensilRequest) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _successMessage.value = null
            try {
                val response = RetrofitClient.utensilApi.updateUtensil(id, request)
                if (response.isSuccessful) {
                    val updatedUtensil = response.body()
                    _selectedUtensil.value = updatedUtensil
                    
                    // Create historic entry
                    if (updatedUtensil != null) {
                        val historicRequest = HistoricUtensilRequest(
                            name = updatedUtensil.name,
                            brand = updatedUtensil.brand,
                            model = updatedUtensil.model,
                            price = updatedUtensil.price,
                            supplierId = updatedUtensil.supplier?.id ?: request.supplierId
                        )
                        try {
                            RetrofitClient.historicUtensilApi.createHistoricUtensil(historicRequest)
                        } catch (e: Exception) {
                            // Log error but don't fail the update
                            _error.value = "Utensil updated but failed to log to history: ${e.message}"
                        }
                    }
                    
                    // Reload list and suppliers, and wait for both to complete
                    try {
                        val loadUtensilsJob = async {
                            val utensilResponse = RetrofitClient.utensilApi.getAll()
                            if (utensilResponse.isSuccessful) {
                                _utensils.value = utensilResponse.body() ?: emptyList()
                            }
                        }
                        
                        val loadSuppliersJob = async {
                            val supplierResponse = RetrofitClient.supplierApi.getAll()
                            if (supplierResponse.isSuccessful) {
                                _suppliers.value = supplierResponse.body() ?: emptyList()
                            }
                        }
                        
                        // Wait for both to complete
                        loadUtensilsJob.await()
                        loadSuppliersJob.await()
                        
                        _successMessage.value = "Utensil updated successfully"
                    } catch (e: Exception) {
                        _error.value = "Updated utensil but failed to refresh lists: ${e.message}"
                    }
                } else {
                    _error.value = "Failed to update utensil"
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
        loadUtensils()
        loadSuppliers()
    }
}
