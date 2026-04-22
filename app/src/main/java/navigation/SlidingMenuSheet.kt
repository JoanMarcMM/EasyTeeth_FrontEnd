package navigation


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.navigation.NavHostController

data class MenuItemData(
    val label: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
fun SlidingMenuSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    navController: NavHostController
) {
    val offsetY by animateDpAsState(
        targetValue = if (isVisible) 0.dp else 350.dp,
        animationSpec = tween<Dp>(durationMillis = 300),
        label = "menu_slide"
    )

    val menuItems = listOf(
        MenuItemData(
            label = "Pacients",
            icon = Icons.Filled.Person,
            route = Routes.PATIENT_MENU_SCREEN,
            color = Color(0xFF90E0D0)
        ),
        MenuItemData(
            label = "Cites",
            icon = Icons.Filled.DateRange,
            route = Routes.APPOINTMENT_MENU_SCREEN,
            color = Color(0xFF98A8E6)
        ),
        MenuItemData(
            label = "Box",
            icon = Icons.Filled.Home,
            route = Routes.BOXES,
            color = Color(0xFFFFB366)
        ),
        MenuItemData(
            label = "Magatzems",
            icon = Icons.Filled.Storage,
            route = Routes.STORAGE_AND_ORDERS_MENU,
            color = Color(0xFF90E0D0)
        ),
        MenuItemData(
            label = "Utensilis",
            icon = Icons.Filled.Settings,
            route = Routes.UTENSILS_AND_SUPPLIERS_MENU,
            color = Color(0xFF98A8E6)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Semi-transparent overlay
        if (isVisible) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = true) { onDismiss() }
            )
        }

        // Menu Sheet - positioned at bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset(y = offsetY)
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            // Menu Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(menuItems.size) { index ->
                    val item = menuItems[index]
                    MenuCard(
                        item = item,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            onDismiss()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun MenuCard(
    item: MenuItemData,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(item.color.copy(alpha = 0.2f))
            .clickable(onClick = onClick)
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.label,
            tint = item.color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = item.color
        )
    }
}
