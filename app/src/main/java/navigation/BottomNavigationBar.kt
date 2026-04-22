package navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val isMenuIcon: Boolean = false
)

@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    val items = listOf(
        BottomNavItem(Routes.HOME, "Inici", Icons.Filled.Home),
        BottomNavItem(Routes.PATIENT_MENU_SCREEN, "Menú", Icons.Filled.Menu, isMenuIcon = true),
        BottomNavItem(Routes.PROFILE, "Perfil", Icons.Filled.Person)
    )

    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry.value?.destination?.route

    // Color palette inspired by EasyTeeth logo (blues)
    val darkBlue = Color(0xFF1B4B7C)
    val mediumBlue = Color(0xFF2E7DB4)
    val lightBlue = Color(0xFF5BA3D0)
    val white = Color.White
    
    NavigationBar(
        containerColor = darkBlue,
        contentColor = white
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = isSelected,
                onClick = {
                    if (item.isMenuIcon) {
                        onMenuClick()
                    } else if (!isSelected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = white,
                    selectedTextColor = white,
                    unselectedIconColor = Color(0xFF90CAF9),
                    unselectedTextColor = Color(0xFF90CAF9),
                    indicatorColor = mediumBlue
                )
            )
        }
    }
}
