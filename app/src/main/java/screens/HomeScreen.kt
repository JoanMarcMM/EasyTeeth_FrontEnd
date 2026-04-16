package screens

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.easyteeth.R
import navigation.Routes

@Composable
fun HomeScreen(navController: NavController) {
    // --- CONFIGURACIÓN DE BARRA DE ESTADO (Para que los iconos del sistema se vean oscuros) ---
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White // Fondo blanco
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .systemBarsPadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. EL LOGO
            Spacer(modifier = Modifier.height(0.dp))
            Image(
                painter = painterResource(id = R.drawable.easy_teeth_sin_fondo),
                contentDescription = "Logo EasyTeeth",
                modifier = Modifier
                    .width(280.dp)
                    .height(180.dp),
                alignment = Alignment.Center
            )

            Spacer(modifier = Modifier.height(0.dp))

//            Todo comentado para poder normalizar los botones, a fin de hacerlo más bonito cuando el backend y la lógica ya esté hecha
//            2. LA CUADRÍCULA DE BOTONES
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(2),
//                contentPadding = PaddingValues(8.dp),
//                verticalArrangement = Arrangement.spacedBy(16.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp),
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                // BOTÓN 1: CALENDARIO
//                item {
//                    HomeMenuCard(
//                        iconResId = R.drawable.calendario,
//                        contentDescription = "Calendario",
//                        backgroundColor = Color(0xFF98A8E6),
//                        onClick = { navController.navigate(Routes.CALENDAR) }
//                    )
//                }
//
//                // BOTÓN 2: PACIENTES
//                item {
//                    HomeMenuCard(
//                        iconResId = R.drawable.pacientes,
//                        contentDescription = "Pacientes",
//                        backgroundColor = Color(0xFF90E0D0),
//                        onClick = { navController.navigate(Routes.PATIENTSLIST) }
//                    )
//                }
//
//                // BOTÓN 3: HERRAMIENTAS / AJUSTES
//                item {
//                    HomeMenuCard(
//                        iconResId = R.drawable.stock,
//                        contentDescription = "Herramientas",
//                        backgroundColor = Color(0xFF98A8E6),
//                        onClick = { navController.navigate(Routes.TREATMENTS) }
//                    )
//                }
//
//                // BOTÓN 4: PERFIL DE USUARIO
//                item {
//                    HomeMenuCard(
//                        iconResId = R.drawable.perfil,
//                        contentDescription = "Perfil",
//                        backgroundColor = Color(0xFF90E0D0),
//                        onClick = { navController.navigate(Routes.PATIENT_MENU_SCREEN) }
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    onClick = { navController.navigate(Routes.PATIENT_MENU_SCREEN) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Pacients",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { navController.navigate(Routes.APPOINTMENT_MENU_SCREEN) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Cites",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = { navController.navigate(Routes.APPOINTMENT_MENU_SCREEN) }, // user/nurse para poder crear usuarios con diferentes rangos
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Usuari/Infermera",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = { navController.navigate(Routes.STORAGE_AND_ORDERS_MENU) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Magatzems i comandes",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
//                Button(
//                    onClick = { navController.navigate(Routes.PROFILE_MENU_SCREEN) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(55.dp),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Text(
//                        text = "Profile",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
//                Button(
//                    onClick = { navController.navigate(Routes.NURSES_MENU_SCREEN) },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(55.dp),
//                    shape = RoundedCornerShape(12.dp)
//                ) {
//                    Text(
//                        text = "Nurses",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.SemiBold
//                    )
//                }
            }
        }
    }
}


@Composable
fun HomeMenuCard(
    iconResId: Int,
    contentDescription: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f) //
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                modifier = Modifier.size(70.dp),
                tint = Color.Black
            )
        }
    }
}