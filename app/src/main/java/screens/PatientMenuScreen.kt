    package screens

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.shape.RoundedCornerShape
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.text.font.FontWeight
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.navigation.NavController
    import navigation.Routes

    @Composable
    fun PatientMenuScreen(navController: NavController) {

        Scaffold { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = "Pacients",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(40.dp))

                Button(
                    onClick = { navController.navigate(Routes.NEW_PATIENT_SCREEN ) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Nou Pacient",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navController.navigate(Routes.PATIENT_LIST_TO_PROFILE) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Llistar Pacients",
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(55.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Tornar",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }