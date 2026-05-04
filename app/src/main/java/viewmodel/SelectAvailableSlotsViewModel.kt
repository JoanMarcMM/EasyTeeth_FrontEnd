package viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.AppointmentApiEndpoints
import api.BoxApiEndpoints
import com.example.easyteeth.model.AppointmentRequest
import com.example.easyteeth.model.Box
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class TimeSlot(
    val period: String,
    val startTime: String,
    val endTime: String,
    val appointmentSlots: List<AppointmentSlot> = emptyList()
)

data class AppointmentSlot(
    val slotStart: String,
    val slotEnd: String,
    val available: Boolean
)

data class AvailableDaySlots(
    val date: LocalDate,
    val dayOfWeek: String,
    val timeSlots: List<TimeSlot>
)

class SelectAvailableSlotsViewModel : ViewModel() {
    private val appointmentApi = RetrofitClient.instance.create(AppointmentApiEndpoints::class.java)
    private val boxApi = RetrofitClient.instance.create(BoxApiEndpoints::class.java)

    // Datos recibidos de la pantalla anterior
    var patientId by mutableStateOf<Long?>(null)
    var treatmentId by mutableStateOf<Long?>(null)
    var odontologistId by mutableStateOf<Long?>(null)
    var motive by mutableStateOf("")
    var selectedShift by mutableStateOf("MORNING")
    var selectedBoxId by mutableStateOf<Long?>(null)

    // Estados de carga
    var isLoadingSlots by mutableStateOf(false)
    var isLoadingBoxes by mutableStateOf(false)
    var isCreating by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Datos disponibles
    var availableSlots = mutableStateListOf<AvailableDaySlots>()
    var availableBoxes = mutableStateListOf<Box>()

    // Selecciones del usuario
    var selectedDate by mutableStateOf<LocalDate?>(null)
    var selectedAppointmentSlot by mutableStateOf<AppointmentSlot?>(null)
    var selectedBox by mutableStateOf<Box?>(null)

    fun initialize(patientId: Long, treatmentId: Long, odontologistId: Long, motive: String, shift: String = "MORNING", boxId: Long = 0L) {
        android.util.Log.d("SelectAvailableSlots", "INITIALIZE called with shift=$shift, boxId=$boxId")

        // Clear all state first
        availableSlots.clear()
        availableBoxes.clear()
        selectedDate = null
        selectedAppointmentSlot = null
        selectedBox = null
        errorMessage = null

        // Set parameters FIRST, before any async operations
        this.patientId = patientId
        this.treatmentId = treatmentId
        this.odontologistId = odontologistId
        this.motive = motive
        this.selectedShift = shift
        this.selectedBoxId = boxId

        android.util.Log.d("SelectAvailableSlots", "State cleared. selectedShift is now: $selectedShift, selectedBoxId: $selectedBoxId")

        viewModelScope.launch {
            val slotsJob = viewModelScope.launch { loadAvailableSlots() }
            val boxesJob = viewModelScope.launch { loadBoxes() }

            slotsJob.join()
            boxesJob.join()
        }
    }

    private suspend fun loadAvailableSlots() {
        isLoadingSlots = true
        errorMessage = null

        try {
            if (odontologistId == null || odontologistId!! <= 0) {
                errorMessage = "Odontólogo inválido"
                return
            }

            val response = appointmentApi.getAvailableSlots(
                odontologistId = odontologistId!!,
                boxId = selectedBoxId
            )

            if (!response.isSuccessful) {
                errorMessage = "Error al obtener disponibilidad: ${response.code()}"
                return
            }

            val slots = response.body() ?: emptyList()

            // Log what the backend returns
            android.util.Log.d("SelectAvailableSlots", "API Response: ${slots.size} days")
            slots.forEach { day ->
                android.util.Log.d("SelectAvailableSlots", "  Day ${day.date}: ${day.timeSlots.size} time slots")
                day.timeSlots.forEach { ts ->
                    android.util.Log.d("SelectAvailableSlots", "    TimeSlot period: '${ts.period}' - ${ts.appointmentSlots.size} appointment slots")
                }
            }

            if (slots.isEmpty()) {
                errorMessage = "Sin disponibilidad en los próximos 30 días"
                availableSlots.clear()
                return
            }

            // Convertir respuesta a AvailableDaySlots
            val convertedSlots = slots.map { slotDto ->
                try {
                    val dateFromString = try {
                        LocalDate.parse(slotDto.date)
                    } catch (e: Exception) {
                        LocalDate.now().plusDays(1)
                    }

                    android.util.Log.d("SelectAvailableSlots", "Date: ${slotDto.date}, DayOfWeek: ${slotDto.dayOfWeek}")
                    android.util.Log.d("SelectAvailableSlots", "TimeSlots count: ${slotDto.timeSlots.size}")
                    android.util.Log.d("SelectAvailableSlots", "Selected Shift: $selectedShift")
                    slotDto.timeSlots.forEach { ts ->
                        android.util.Log.d("SelectAvailableSlots", "  Available TimeSlot period: '${ts.period}' - matches shift? ${ts.period.equals(selectedShift, ignoreCase = true)}")
                    }

                    AvailableDaySlots(
                        date = dateFromString,
                        dayOfWeek = slotDto.dayOfWeek,
                        timeSlots = slotDto.timeSlots
                            .filter { it.period.equals(selectedShift, ignoreCase = true) }
                            .map { timeSlot ->
                                val slots = (timeSlot.appointmentSlots ?: emptyList()).map { aptSlot ->
                                    android.util.Log.d("SelectAvailableSlots", "Slot: ${aptSlot.slotStart} - ${aptSlot.slotEnd}, Available: ${aptSlot.available}")
                                    AppointmentSlot(
                                        slotStart = aptSlot.slotStart,
                                        slotEnd = aptSlot.slotEnd,
                                        available = aptSlot.available
                                    )
                                }

                                android.util.Log.d("SelectAvailableSlots", "${timeSlot.period}: ${slots.size} slots")

                                TimeSlot(
                                    period = timeSlot.period,
                                    startTime = timeSlot.startTime,
                                    endTime = timeSlot.endTime,
                                    appointmentSlots = slots
                                )
                            }
                    )
                } catch (e: Exception) {
                    errorMessage = "Error procesando disponibilidad: ${e.message}"
                    android.util.Log.e("SelectAvailableSlots", "Error: ${e.message}", e)
                    e.printStackTrace()
                    null
                }
            }.filterNotNull()
                // Also filter to only keep days that have the selected shift
                .filter { it.timeSlots.isNotEmpty() }

            android.util.Log.d("SelectAvailableSlots", "After filtering by shift, days with slots: ${convertedSlots.size}")
            convertedSlots.forEach { day ->
                android.util.Log.d("SelectAvailableSlots", "Day ${day.date}: ${day.timeSlots.size} time periods")
                day.timeSlots.forEach { ts ->
                    android.util.Log.d("SelectAvailableSlots", "  Period: ${ts.period} with ${ts.appointmentSlots.size} appointment slots")
                }
            }

            availableSlots.clear()
            availableSlots.addAll(convertedSlots)

            // Auto-select first available day and first available slot
            if (convertedSlots.isNotEmpty()) {
                selectedDate = convertedSlots[0].date
                // Find first available appointment slot across all time periods
                for (timePeriod in convertedSlots[0].timeSlots) {
                    val firstAvailable = timePeriod.appointmentSlots.firstOrNull { it.available }
                    if (firstAvailable != null) {
                        selectedAppointmentSlot = firstAvailable
                        break
                    }
                }
            }

        } catch (e: Exception) {
            errorMessage = "Error al cargar disponibilidad: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingSlots = false
        }
    }

    private suspend fun loadBoxes() {
        isLoadingBoxes = true

        try {
            val response = boxApi.getAllBoxes()
            if (response.isSuccessful) {
                val boxes = response.body() ?: emptyList()
                availableBoxes.clear()
                availableBoxes.addAll(boxes)

                // Auto-select first box if available
                if (boxes.isNotEmpty()) {
                    selectedBox = boxes[0]
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al cargar cajas: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingBoxes = false
        }
    }

    fun isReadyToProceed(): Boolean {
        return selectedDate != null &&
                selectedAppointmentSlot != null &&
                selectedBox != null &&
                patientId != null &&
                treatmentId != null &&
                odontologistId != null
    }

    fun createAppointment(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!isReadyToProceed()) {
            onError("Por favor completa todos los campos")
            return
        }

        viewModelScope.launch {
            isCreating = true
            errorMessage = null

            try {
                // Construir DateTime combinando la fecha seleccionada y la hora del slot seleccionado
                val appointmentSlot = selectedAppointmentSlot ?: return@launch
                val slotStartTime = appointmentSlot.slotStart
                val timeParts = slotStartTime.split(":")
                val hour = if (timeParts.isNotEmpty()) timeParts[0].toIntOrNull() ?: 8 else 8
                val minute = if (timeParts.size > 1) timeParts[1].toIntOrNull() ?: 0 else 0

                val calendar = Calendar.getInstance().apply {
                    val selectedDateLocal = selectedDate ?: LocalDate.now()
                    set(Calendar.YEAR, selectedDateLocal.year)
                    set(Calendar.MONTH, selectedDateLocal.monthValue - 1)
                    set(Calendar.DAY_OF_MONTH, selectedDateLocal.dayOfMonth)
                    set(Calendar.HOUR_OF_DAY, hour)
                    set(Calendar.MINUTE, minute)
                }

                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val dateTimeString = formatter.format(calendar.time)

                // Crear request
                val request = AppointmentRequest(
                    motive = motive,
                    date = dateTimeString,
                    patientId = patientId ?: 0L,
                    boxId = selectedBox?.id ?: 0L,
                    odontologistId = odontologistId ?: 0L,
                    treatmentId = treatmentId ?: 0L
                )

                // Llamar al API
                val response = appointmentApi.createAppointment(request)

                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onError("Error al crear cita: ${response.code()}")
                    errorMessage = "Error al crear cita: ${response.code()}"
                }
            } catch (e: Exception) {
                val errorMsg = "Excepción: ${e.message}"
                onError(errorMsg)
                errorMessage = errorMsg
                e.printStackTrace()
            } finally {
                isCreating = false
            }
        }
    }
}
