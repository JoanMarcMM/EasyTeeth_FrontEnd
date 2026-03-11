package viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.AppointmentApiEndpoints
import kotlinx.coroutines.launch
import com.example.easyteeth.model.Appointment
import java.text.SimpleDateFormat
import java.util.*

class AppointmentViewModel : ViewModel() {
    // Inicializamos el servicio de API usando tu RetrofitClient
    private val api = RetrofitClient.instance.create(AppointmentApiEndpoints::class.java)

    // Lista observable que la UI de Compose escuchará
    var appointmentsByDate = mutableStateListOf<Appointment>()

    // Estados para controlar la carga y errores en la UI
    var isLoading by mutableStateOf(false)
    var errorOccurred by mutableStateOf(false)

    /**
     * Llama al backend para obtener las citas de un día específico.
     * @param millis Milisegundos obtenidos del DatePickerState
     */
    fun fetchAppointments(millis: Long?) {
        if (millis == null) return

        // CONFIGURACIÓN DE FECHA:
        // El DatePicker de Material 3 devuelve milisegundos en UTC.
        // Usamos UTC aquí para evitar que el formateador reste horas y cambie el día.
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val dateString = formatter.format(Date(millis))

        viewModelScope.launch {
            isLoading = true
            errorOccurred = false

            try {
                // Llamada al endpoint: /appointment/dateBetween/{date}
                val response = api.getAppointmentsByDay(dateString)

                // Limpiamos la lista actual antes de recibir los nuevos datos
                appointmentsByDate.clear()

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        appointmentsByDate.addAll(body)
                    }
                } else if (response.code() == 404) {
                    // Tu backend devuelve 404 si findByDateBetween no encuentra nada.
                    // No es un error crítico, simplemente significa que el día está libre.
                    // La lista se queda vacía y la UI mostrará "No hay citas".
                } else {
                    errorOccurred = true
                }
            } catch (e: Exception) {
                // Error de conexión (Servidor apagado, sin internet, etc.)
                errorOccurred = true
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}