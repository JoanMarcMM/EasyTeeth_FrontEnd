package viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyteeth.api.BoxApiEndpoints
import com.example.easyteeth.model.Box
import com.example.easyteeth.model.StockBox
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

    fun fetchMaterials(boxId: Long, date: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = api.getMaterialsByDay(boxId, date)
                if (response.isSuccessful) {
                    _materials.value = response.body() ?: emptyList()
                } else {
                    Log.e("BoxViewModel", "Error del servidor trayendo materiales: ${response.code()}")
                    _materials.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("BoxViewModel", "Error de red trayendo materiales: ${e.localizedMessage}")
                _materials.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateStockStatus(boxId: Long, dateMillis: Long, status: Boolean, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(dateMillis))
            try {
                val response = api.updateStockStatus(boxId, dateString, status)
                onResult(response.isSuccessful)
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }
}