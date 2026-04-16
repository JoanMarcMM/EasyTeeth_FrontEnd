package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.UtensilOrderResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class OrdersListViewModel : ViewModel() {
    private val _allOrders = MutableStateFlow<List<UtensilOrderResponse>>(emptyList())
    val allOrders: StateFlow<List<UtensilOrderResponse>> = _allOrders

    private val _filteredOrders = MutableStateFlow<List<UtensilOrderResponse>>(emptyList())
    val filteredOrders: StateFlow<List<UtensilOrderResponse>> = _filteredOrders

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Filter state
    private val _arrivalFilter = MutableStateFlow<String>("all") // "all", "arrived", "not_arrived"
    val arrivalFilter: StateFlow<String> = _arrivalFilter

    private val _sortBy = MutableStateFlow<String>("date") // "date", "date_asc", "storage"
    val sortBy: StateFlow<String> = _sortBy

    private val _selectedStorageFilter = MutableStateFlow<Long?>(null)
    val selectedStorageFilter: StateFlow<Long?> = _selectedStorageFilter

    private val _availableStorages = MutableStateFlow<List<Long>>(emptyList())
    val availableStorages: StateFlow<List<Long>> = _availableStorages

    fun loadAllOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitClient.utensilOrderApi.getAllOrders()
                if (response.isSuccessful) {
                    val orders = response.body() ?: emptyList()
                    _allOrders.value = orders
                    
                    // Extract unique storage IDs
                    _availableStorages.value = orders.map { it.storage_id }.distinct().sorted()
                    
                    // Apply filters
                    applyFilters()
                } else {
                    _error.value = "Error: ${response.code()} - ${response.message()}"
                    android.util.Log.e("OrdersListViewModel", "API Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
                android.util.Log.e("OrdersListViewModel", "Exception: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setArrivalFilter(filter: String) {
        _arrivalFilter.value = filter
        applyFilters()
    }

    fun setSortBy(sort: String) {
        _sortBy.value = sort
        applyFilters()
    }

    fun setStorageFilter(storageId: Long?) {
        _selectedStorageFilter.value = storageId
        applyFilters()
    }

    private fun applyFilters() {
        var filtered = _allOrders.value

        // Apply arrival filter
        filtered = when (_arrivalFilter.value) {
            "arrived" -> filtered.filter { it.arrived }
            "not_arrived" -> filtered.filter { !it.arrived }
            else -> filtered
        }

        // Apply storage filter
        _selectedStorageFilter.value?.let { storageId ->
            filtered = filtered.filter { it.storage_id == storageId }
        }

        // Apply sorting
        filtered = when (_sortBy.value) {
            "date_asc" -> filtered.sortedBy { parseDate(it.orderDate) }
            "storage" -> filtered.sortedBy { it.storage_id }
            else -> {
                // Default: most recent first, with arrived at the end
                filtered.sortedWith(compareBy(
                    { it.arrived }, // not arrived first
                    { -parseDate(it.orderDate) } // then by date descending
                ))
            }
        }

        _filteredOrders.value = filtered
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val localDate = LocalDate.parse(dateStr, formatter)
            localDate.toEpochDay()
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun markOrderArrived(orderId: Long): Result<String> {
        return try {
            val response = RetrofitClient.utensilOrderApi.markOrderArrived(orderId)
            if (response.isSuccessful) {
                // Reload orders after marking as arrived
                loadAllOrders()
                Result.success("Orden marcada como aribada")
            } else {
                Result.failure(Exception("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
