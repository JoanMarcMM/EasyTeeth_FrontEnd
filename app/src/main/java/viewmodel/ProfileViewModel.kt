package viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.ChangePasswordRequest
import api.RetrofitClient
import api.UserApiEndpoints
import kotlinx.coroutines.launch
import com.example.easyteeth.model.User
import com.example.easyteeth.state.UserStateHolder

class ProfileViewModel : ViewModel() {

    var user: User? by mutableStateOf(UserStateHolder.currentUser)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var successMessage by mutableStateOf<String?>(null)

    // Password change fields
    var oldPassword by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var passwordChangeInProgress by mutableStateOf(false)

    private val api = RetrofitClient.instance.create(UserApiEndpoints::class.java)

    init {
        // Load user info if not already in state
        if (user == null) {
            loadUserInfo()
        } else {
            user = UserStateHolder.currentUser
        }
    }

    fun loadUserInfo() {
        val currentUserId = UserStateHolder.currentUser?.id
        if (currentUserId == null) {
            errorMessage = "Cap usuari ha iniciat sessi\u00f3"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = api.getUserById(currentUserId)
                if (response.isSuccessful && response.body() != null) {
                    user = response.body()!!
                    UserStateHolder.setUser(user!!)
                } else {
                    errorMessage = "Error al carregar la informaci\u00f3 de l'usuari"
                }
            } catch (e: Exception) {
                errorMessage = "Error de connexi\u00f3: ${e.localizedMessage ?: e.message ?: "Error desconegut"}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(onSuccess: () -> Unit) {
        // Validation
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            errorMessage = "Si us plau, omple tots els camps"
            return
        }

        if (newPassword != confirmPassword) {
            errorMessage = "Les contrasenyes no coincideixen"
            return
        }

        val currentUserId = UserStateHolder.currentUser?.id
        if (currentUserId == null) {
            errorMessage = "Cap usuari ha iniciat sessi\u00f3"
            return
        }

        viewModelScope.launch {
            passwordChangeInProgress = true
            errorMessage = null
            successMessage = null

            try {
                val request = ChangePasswordRequest(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )

                val response = api.changePassword(currentUserId, request)

                if (response.isSuccessful) {
                    successMessage = "Contrasenya canviada correctament"
                    oldPassword = ""
                    newPassword = ""
                    confirmPassword = ""
                    onSuccess()
                } else {
                    // Try to read error body
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = if (!errorBody.isNullOrBlank()) errorBody else response.message()
                    errorMessage = "Error al canviar contrasenya: $errorMsg"
                }
            } catch (e: Exception) {
                errorMessage = "Error de connexi\u00f3: ${e.localizedMessage ?: e.message ?: "Error desconegut"}"
                e.printStackTrace()
            } finally {
                passwordChangeInProgress = false
            }
        }
    }

    fun logout(onSuccess: () -> Unit) {
        UserStateHolder.clearUser()
        oldPassword = ""
        newPassword = ""
        confirmPassword = ""
        onSuccess()
    }
}
