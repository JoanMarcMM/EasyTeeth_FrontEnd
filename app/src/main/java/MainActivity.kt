package com.example.easyteeth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.easyteeth.screens.AppointmentSearcherScreen
import com.example.easyteeth.screens.OdontogramScreen
import com.example.easyteeth.screens.PatientListToProfileScreen
import com.example.easyteeth.screens.PatientSelectorScreen
import com.example.easyteeth.screens.ToothDetailScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import api.RetrofitClient
import com.example.easyteeth.screens.BoxCalendarScreen
import viewmodel.BoxViewModel
import com.example.easyteeth.screens.BoxListScreen
import navigation.Routes
import screens.CalendarScreen
import screens.HomeScreen
import screens.LoginScreen
import screens.NewBackgroundScreen
import screens.NewPatientScreen
import screens.PatientMenuScreen
import screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Routes.LOGIN
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(navController)
                }

                composable(Routes.HOME) {
                    HomeScreen(navController)
                }

                composable(Routes.CALENDAR) {
                    CalendarScreen(navController)
                }

                composable(Routes.PATIENT_MENU_SCREEN) {
                    PatientMenuScreen(navController)
                }

                composable(Routes.NEW_PATIENT_SCREEN) {
                    NewPatientScreen(navController)
                }

                composable(
                    route = Routes.NEW_BACKGROUND_SCREEN,
                    arguments = listOf(
                        navArgument("patientId") {
                            type = NavType.LongType
                        }
                    )
                ) { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L

                    NewBackgroundScreen(
                        navController = navController,
                        patientId = patientId
                    )
                }
                composable(Routes.APPOINTMENT_SEARCHER) {
                    AppointmentSearcherScreen(navController)

                }

                composable(Routes.PATIENTS_APPOINTMENT) {
                    PatientSelectorScreen(navController)

                }

                composable(Routes.PATIENT_LIST_TO_PROFILE) {
                    PatientListToProfileScreen(navController)

                }
                composable(
                    route = Routes.PATIENT_PROFILE_SCREEN,
                    arguments = listOf(
                        navArgument("patientId") {
                            type = NavType.LongType
                        }
                    )
                ) { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L

                    PatientProfileScreen(
                        navController = navController,
                        patientId = patientId
                    )
                }
                composable(
                    route = Routes.UPDATE_PATIENT_SCREEN,
                    arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                    UpdatePatientScreen(
                        navController = navController,
                        patientId = patientId
                    )
                }

                composable(
                    route = Routes.UPDATE_BACKGROUND_SCREEN,
                    arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                ) { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getLong("patientId") ?: 0L
                    UpdateBackgroundScreen(
                        navController = navController,
                        patientId = patientId
                    )
                }

                composable(
                    route = Routes.ODONTOGRAM_SCREEN,
                    arguments = listOf(navArgument("patientId") { type = NavType.LongType })
                ) {
                    val patientId = it.arguments?.getLong("patientId") ?: 0L
                    OdontogramScreen(navController, patientId)
                }

                composable(
                    route = Routes.TOOTH_DETAIL_SCREEN,
                    arguments = listOf(
                        navArgument("patientId") { type = NavType.LongType },
                        navArgument("toothId") { type = NavType.LongType }
                    )
                ) {
                    val patientId = it.arguments?.getLong("patientId") ?: 0L
                    val toothId = it.arguments?.getLong("toothId") ?: 0L

                    ToothDetailScreen(navController, patientId, toothId)
                }
                composable("patientImages/{patientId}") { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getString("patientId")?.toLong() ?: 0L
                    PatientImagesScreen(navController, patientId)
                }

                composable("patientDocuments/{patientId}") { backStackEntry ->
                    val patientId = backStackEntry.arguments?.getString("patientId")?.toLong() ?: 0L
                    PatientDocumentsScreen(navController, patientId)
                }
                composable(Routes.BOXES) {
                    val boxViewModel = viewModel<BoxViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return BoxViewModel(RetrofitClient.boxApi) as T
                            }
                        }
                    )
                    BoxListScreen(navController = navController, viewModel = boxViewModel)
                }
                composable(
                    route = Routes.BOX_CALENDAR_SCREEN,
                    arguments = listOf(
                        navArgument("boxId") { type = NavType.LongType },
                        navArgument("numBox") { type = NavType.IntType }
                    )
                ) { backStackEntry ->
                    val boxId = backStackEntry.arguments?.getLong("boxId") ?: 0L
                    val numBox = backStackEntry.arguments?.getInt("numBox") ?: 0
                    val boxViewModel = viewModel<BoxViewModel>(
                        factory = object : ViewModelProvider.Factory {
                            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                return BoxViewModel(RetrofitClient.boxApi) as T
                            }
                        }
                    )

                    BoxCalendarScreen(
                        navController = navController,
                        boxId = boxId,
                        numBox = numBox,
                        viewModel = boxViewModel
                    )
                }
            }
        }
    }
}