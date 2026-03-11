package com.example.easyteeth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import navigation.Routes
import screens.HomeScreen
import screens.LoginScreen
import screens.CalendarScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // ... dentro de tu MainActivity ...
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.HOME // <--- Esto garantiza que el Login salga primero
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(navController)
                }
                composable(Routes.HOME) {
                    HomeScreen(navController)
                }
                composable(Routes.CALENDAR){
                    CalendarScreen(navController)

                }
            }
        }
    }
}