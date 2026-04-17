package viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import com.example.easyteeth.api.BoxApiEndpoints
import com.example.easyteeth.model.Box
import com.example.easyteeth.model.ItemReductionRequest
import com.example.easyteeth.model.StockBox
import com.example.easyteeth.model.StockReductionRequest
import com.example.easyteeth.model.StockStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BoxViewModel(
    private val api: BoxApiEndpoints
) : ViewModel() {

    private val _boxes = MutableStateFlow<List<Box>>(emptyList())
    val boxes: StateFlow<List<Box>> = _boxes

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _materials = MutableStateFlow<List<StockBox>>(emptyList())
    val materials: StateFlow<List<StockBox>> = _materials

    private val _hasInsufficientStock = MutableStateFlow(false)
    val hasInsufficientStock: StateFlow<Boolean> = _hasInsufficientStock

    private val _insufficientUtensilsList = MutableStateFlow<List<String>>(emptyList())
    val insufficientUtensilsList: StateFlow<List<String>> = _insufficientUtensilsList

    init {
        fetchBoxes()
    }

    private fun fetchBoxes() {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.getAllBoxes()
                if (response.isSuccessful) {
                    _boxes.value = response.body() ?: emptyList()
                } else {
                    Log.e("BoxViewModel", "Error del servidor: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("BoxViewModel", "Error de red: ${e.localizedMessage}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchMaterials(boxId: Long, date: String, skipValidation: Boolean = false) {
        viewModelScope.launch {
            _loading.value = true
            Log.d("BoxViewModel", "fetchMaterials called: boxId=$boxId, date=$date, skipValidation=$skipValidation")
            try {
                val response = api.getMaterialsByDay(boxId, date)
                Log.d("BoxViewModel", "API Response: successful=${response.isSuccessful}, code=${response.code()}")
                
                if (response.isSuccessful) {
                    val materials = response.body() ?: emptyList()
                    Log.d("BoxViewModel", "Materials fetched: count=${materials.size}")
                    materials.forEach { material ->
                        Log.d("BoxViewModel", "  - ${material.utensil.name} x${material.quantity}, stocked=${material.stocked}")
                    }
                    
                    _materials.value = materials
                    
                    // Check if all materials are already stocked
                    val allStocked = materials.isNotEmpty() && materials.all { it.stocked }
                    
                    // After fetching materials, validate stock availability ONLY if:
                    // - Not skipping validation AND
                    // - Materials are NOT all already stocked
                    if (_materials.value.isNotEmpty() && !skipValidation && !allStocked) {
                        validateStockAvailability()
                    } else {
                        // Clear validation when skipping or when all items are already stocked
                        _hasInsufficientStock.value = false
                        _insufficientUtensilsList.value = emptyList()
                    }
                } else {
                    Log.e("BoxViewModel", "Error del servidor trayendo materiales: ${response.code()}")
                    Log.e("BoxViewModel", "Error body: ${response.errorBody()?.string()}")
                    _materials.value = emptyList()
                    _hasInsufficientStock.value = false
                    _insufficientUtensilsList.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("BoxViewModel", "Error de red trayendo materiales: ${e.localizedMessage}")
                e.printStackTrace()
                _materials.value = emptyList()
                _hasInsufficientStock.value = false
                _insufficientUtensilsList.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    private fun validateStockAvailability() {
        viewModelScope.launch {
            try {
                // Get all storages
                val storagesResponse = RetrofitClient.storageApi.getAllStorages()
                if (!storagesResponse.isSuccessful || storagesResponse.body() == null) {
                    Log.e("BoxViewModel", "Error fetching storages")
                    _hasInsufficientStock.value = false
                    _insufficientUtensilsList.value = emptyList()
                    return@launch
                }

                val storages = storagesResponse.body()!!
                val insufficientItems = mutableListOf<String>()

                // Only validate materials that are NOT already stocked
                val materialsToValidate = _materials.value.filter { !it.stocked }
                
                Log.d("BoxViewModel", "Materials to validate: ${materialsToValidate.size} (already stocked: ${_materials.value.filter { it.stocked }.size})")

                // For each material that is NOT stocked, check if we have enough in all storages combined
                for (material in materialsToValidate) {
                    var totalAvailable = 0
                    val utensilId = material.utensil.id ?: continue
                    val requiredQuantity = material.quantity

                    // Get stock from each storage
                    for (storage in storages) {
                        try {
                            val stockResponse = RetrofitClient.stockStorageApi.getStockByStorage(storage.id ?: 0)
                            if (stockResponse.isSuccessful && stockResponse.body() != null) {
                                val stocks = stockResponse.body()!!
                                // Find this utensil in this storage
                                val utensilStock = stocks.find { it.utensil.id == utensilId }
                                if (utensilStock != null) {
                                    totalAvailable += utensilStock.quantity
                                }
                            }
                        } catch (e: Exception) {
                            Log.e("BoxViewModel", "Error fetching stock for storage ${storage.id}: ${e.localizedMessage}")
                        }
                    }

                    // Check if we have enough total
                    if (totalAvailable < requiredQuantity) {
                        insufficientItems.add(
                            "${material.utensil.name} (Necesarios: $requiredQuantity, Disponibles: $totalAvailable)"
                        )
                    }
                }

                _hasInsufficientStock.value = insufficientItems.isNotEmpty()
                _insufficientUtensilsList.value = insufficientItems

                if (insufficientItems.isNotEmpty()) {
                    Log.w("BoxViewModel", "Insufficient stock detected: $insufficientItems")
                } else {
                    Log.i("BoxViewModel", "All materials have sufficient stock")
                }
            } catch (e: Exception) {
                Log.e("BoxViewModel", "Error validating stock: ${e.localizedMessage}")
                _hasInsufficientStock.value = false
                _insufficientUtensilsList.value = emptyList()
            }
        }
    }

    fun updateStockStatus(boxId: Long, dateMillis: Long, status: Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateMillis))
            try {
                // Update the box stock status
                val response = api.updateStockStatus(boxId, dateString, status)
                
                if (!response.isSuccessful) {
                    onResult(false)
                    return@launch
                }

                // If status is true (stocking confirmed), reduce inventory from storage
                if (status && _materials.value.isNotEmpty()) {
                    val reductionItems = _materials.value.map { material ->
                        ItemReductionRequest(
                            utensilId = material.utensil.id ?: 0,
                            quantity = material.quantity
                        )
                    }

                    val reductionRequest = StockReductionRequest(
                        boxId = boxId,
                        date = dateString,
                        items = reductionItems
                    )

                    try {
                        val reductionResponse = RetrofitClient.stockStorageApi.reduceStockForBox(reductionRequest)
                        if (reductionResponse.isSuccessful) {
                            Log.i("BoxViewModel", "Stock reduced successfully for box $boxId")
                            // Clear the insufficient stock warning since we successfully stocked
                            _hasInsufficientStock.value = false
                            _insufficientUtensilsList.value = emptyList()
                            // Refresh materials WITHOUT validating (skip validation after successful stocking)
                            fetchMaterials(boxId, dateString, skipValidation = true)
                            onResult(true)
                        } else {
                            Log.e("BoxViewModel", "Error reducing stock: ${reductionResponse.code()}")
                            onResult(false)
                        }
                    } catch (e: Exception) {
                        Log.e("BoxViewModel", "Exception reducing stock: ${e.localizedMessage}")
                        onResult(false)
                    }
                } else if (!status && _materials.value.isNotEmpty()) {
                    // Status is false (canceling stocking) - restore stock to storage
                    val restoreItems = _materials.value.filter { it.stocked }.map { material ->
                        ItemReductionRequest(
                            utensilId = material.utensil.id ?: 0,
                            quantity = material.quantity
                        )
                    }

                    if (restoreItems.isNotEmpty()) {
                        val restoreRequest = StockReductionRequest(
                            boxId = boxId,
                            date = dateString,
                            items = restoreItems
                        )

                        try {
                            val restoreResponse = RetrofitClient.stockStorageApi.restoreStockFromBox(restoreRequest)
                            if (restoreResponse.isSuccessful) {
                                Log.i("BoxViewModel", "Stock restored successfully to storage for box $boxId")
                                _hasInsufficientStock.value = false
                                _insufficientUtensilsList.value = emptyList()
                                fetchMaterials(boxId, dateString, skipValidation = false)
                                onResult(true)
                            } else {
                                Log.e("BoxViewModel", "Error restoring stock: ${restoreResponse.code()}")
                                onResult(false)
                            }
                        } catch (e: Exception) {
                            Log.e("BoxViewModel", "Exception restoring stock: ${e.localizedMessage}")
                            onResult(false)
                        }
                    } else {
                        // No stocked items to restore
                        _hasInsufficientStock.value = false
                        _insufficientUtensilsList.value = emptyList()
                        fetchMaterials(boxId, dateString, skipValidation = false)
                        onResult(true)
                    }
                } else {
                    // Edge case - no materials
                    _hasInsufficientStock.value = false
                    _insufficientUtensilsList.value = emptyList()
                    onResult(true)
                }
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}