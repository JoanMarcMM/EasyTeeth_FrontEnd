package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.model.Utensil
import com.example.easyteeth.model.UtensilOrder
import com.example.easyteeth.model.OrderItemRequest
import com.example.easyteeth.model.UtensilOrderRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UtensilOrderViewModel : ViewModel() {
    companion object {
        // Global storage to persist selections across ViewModel instances
        private val globalSelectedQuantities = MutableStateFlow<Map<Long, Int>>(emptyMap())
        private val globalStockItems = MutableStateFlow<List<com.example.easyteeth.model.StockStorage>>(emptyList())

        fun getGlobalSelectedQuantities(): StateFlow<Map<Long, Int>> = globalSelectedQuantities
        fun getGlobalStockItems(): StateFlow<List<com.example.easyteeth.model.StockStorage>> = globalStockItems
        
        fun clearGlobalSelections() {
            globalSelectedQuantities.value = emptyMap()
            globalStockItems.value = emptyList()
        }
    }

    private val _stockItems = MutableStateFlow<List<com.example.easyteeth.model.StockStorage>>(emptyList())
    val stockItems: StateFlow<List<com.example.easyteeth.model.StockStorage>> = _stockItems

    private val _selectedQuantities = MutableStateFlow<Map<Long, Int>>(emptyMap())
    val selectedQuantities: StateFlow<Map<Long, Int>> = _selectedQuantities

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
                    globalStockItems.value = _stockItems.value
                    // Restore global selections
                    _selectedQuantities.value = globalSelectedQuantities.value
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

    fun updateQuantity(utensilId: Long, quantity: Int) {
        val current = _selectedQuantities.value.toMutableMap()
        if (quantity > 0) {
            current[utensilId] = quantity
        } else {
            current.remove(utensilId)
        }
        _selectedQuantities.value = current
        globalSelectedQuantities.value = current  // Sync with global
    }

    fun incrementQuantity(utensilId: Long) {
        val current = _selectedQuantities.value.toMutableMap()
        val newQuantity = (current[utensilId] ?: 0) + 1
        current[utensilId] = newQuantity
        _selectedQuantities.value = current
        globalSelectedQuantities.value = current  // Sync with global
    }

    fun decrementQuantity(utensilId: Long) {
        val current = _selectedQuantities.value.toMutableMap()
        val newQuantity = (current[utensilId] ?: 0) - 1
        if (newQuantity > 0) {
            current[utensilId] = newQuantity
        } else {
            current.remove(utensilId)
        }
        _selectedQuantities.value = current
        globalSelectedQuantities.value = current  // Sync with global
    }

    fun getSelectedOrders(): List<UtensilOrder> {
        // Use global selections if available, otherwise use local
        val selections = if (globalSelectedQuantities.value.isNotEmpty()) {
            globalSelectedQuantities.value
        } else {
            _selectedQuantities.value
        }
        
        val items = if (globalStockItems.value.isNotEmpty()) {
            globalStockItems.value
        } else {
            _stockItems.value
        }
        
        return items
            .filter { selections.containsKey(it.utensil.id) }
            .map { stock ->
                UtensilOrder(
                    utensil = stock.utensil,
                    quantity = selections[stock.utensil.id] ?: 0
                )
            }
    }

    fun clearOrder() {
        _selectedQuantities.value = emptyMap()
        globalSelectedQuantities.value = emptyMap()
        clearGlobalSelections()
    }

    fun getTotalItems(): Int {
        val selections = if (globalSelectedQuantities.value.isNotEmpty()) {
            globalSelectedQuantities.value
        } else {
            _selectedQuantities.value
        }
        return selections.values.sum()
    }

    suspend fun submitOrder(storageId: Long): Result<String> {
        return try {
            val orderItems = getSelectedOrders().map { order ->
                OrderItemRequest(
                    utensilId = order.utensil.id!!,
                    quantity = order.quantity,
                    unitPrice = order.utensil.price
                )
            }

            val orderRequest = UtensilOrderRequest(
                orderDate = java.time.LocalDate.now().toString(),
                storageId = storageId,
                orderItems = orderItems
            )

            val response = RetrofitClient.utensilOrderApi.createOrder(orderRequest)
            if (response.isSuccessful) {
                clearOrder()
                Result.success("Order created successfully!")
            } else {
                Result.failure(Exception("Failed to create order"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
