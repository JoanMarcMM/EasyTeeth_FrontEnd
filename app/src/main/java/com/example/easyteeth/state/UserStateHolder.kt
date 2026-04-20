package com.example.easyteeth.state

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.easyteeth.model.User

/**
 * Singleton object to store the currently logged-in user
 * This allows access to the user from any screen without passing it through navigation
 */
object UserStateHolder {
    var currentUser: User? by mutableStateOf(null)

    fun setUser(user: User) {
        currentUser = user
    }

    fun clearUser() {
        currentUser = null
    }

    fun isLoggedIn(): Boolean = currentUser != null
}
