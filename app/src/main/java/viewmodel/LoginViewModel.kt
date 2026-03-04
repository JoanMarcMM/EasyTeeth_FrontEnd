package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.UserApiEndpoints
import kotlinx.coroutines.launch
import model.User

class LoginViewModel : ViewModel() {

    // 1. Estados de los campos de texto
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    // 2. Estados de control de la UI
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    // Creamos la instancia de la API usando nuestro RetrofitClient
    private val api = RetrofitClient.instance.create(UserApiEndpoints::class.java)

    /**
     * Función que se ejecuta al pulsar el botón de login
     * @param onSuccess Callback que se ejecuta si el backend devuelve un 200 OK
     */
    fun onLoginClick(onSuccess: (User) -> Unit) {
        // Validación básica antes de llamar al servidor
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Por favor, introduce usuario y contraseña"
            return
        }

        // Iniciamos una corrutina para no bloquear el hilo principal (UI)
        viewModelScope.launch {
            isLoading = true
            errorMessage = null // Limpiamos errores previos

            try {
                // Creamos el objeto User para enviar al backend
                val userToLogin = User(username = username, password = password)

                // Hacemos la llamada al backend (Eclipse)
                val response = api.login(userToLogin)

                if (response.isSuccessful && response.body() != null) {
                    // Si el servidor responde 200 OK, ejecutamos el éxito
                    onSuccess(response.body()!!)
                } else {
                    // Si responde 401 Unauthorized u otros errores
                    errorMessage = "Usuario o contraseña incorrectos"
                }
            } catch (e: Exception) {
                // Si el servidor no está encendido o no hay internet
                errorMessage = "Error de conexión: Verifica que el backend esté corriendo"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }
}
