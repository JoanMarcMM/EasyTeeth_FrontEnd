package screens

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Odontogram
import com.example.easyteeth.screens.ToothCanvasDiagram
import com.example.easyteeth.utils.OdontoBlack
import com.example.easyteeth.utils.OdontoBlue
import com.example.easyteeth.utils.OdontoGray
import com.example.easyteeth.utils.OdontoGreen
import com.example.easyteeth.utils.OdontoRed
import com.example.easyteeth.utils.OdontoYellow
import com.example.easyteeth.utils.ToothCatalogItem
import com.example.easyteeth.utils.getSimpleOdontogramColor
import com.example.easyteeth.utils.isMissingTooth
import com.example.easyteeth.utils.lowerChildLeft
import com.example.easyteeth.utils.lowerChildRight
import com.example.easyteeth.utils.lowerPermanentLeft
import com.example.easyteeth.utils.lowerPermanentRight
import com.example.easyteeth.utils.toothHasFiveSides
import com.example.easyteeth.utils.upperChildLeft
import com.example.easyteeth.utils.upperChildRight
import com.example.easyteeth.utils.upperPermanentLeft
import com.example.easyteeth.utils.upperPermanentRight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OdontogramScreen(
    navController: NavController,
    patientId: Long
) {
    var odontograms by remember { mutableStateOf<List<Odontogram>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showChildDentition by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    fun loadOdontogram() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.odontogramApi.getByPatient(patientId)
                if (response.isSuccessful) {
                    odontograms = response.body() ?: emptyList()
                } else if (response.code() == 404) {
                    odontograms = emptyList()
                } else {
                    errorMessage = "No s'ha pogut carregar l'odontograma"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error de connexió"
            } finally {
                isLoading = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                loadOdontogram()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Odontograma",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        style = if (isLandscape) {
                            MaterialTheme.typography.titleSmall
                        } else {
                            MaterialTheme.typography.titleLarge
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF1B4B7C)
                ),
                modifier = if (isLandscape) Modifier
                    .height(48.dp)
                    .padding(vertical = 0.dp) else Modifier
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = if (isLandscape) 14.dp else 20.dp,
                    vertical = if (isLandscape) 8.dp else 20.dp
                ),
            verticalArrangement = Arrangement.spacedBy(if (isLandscape) 12.dp else 16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    if (isLandscape) {
                        LandscapeOdontogramContent(
                            odontograms = odontograms,
                            patientId = patientId,
                            navController = navController,
                            showChildDentition = showChildDentition,
                            onShowChildDentitionChange = { showChildDentition = it }
                        )
                    } else {
                        PortraitOdontogramContent(
                            odontograms = odontograms,
                            patientId = patientId,
                            navController = navController,
                            showChildDentition = showChildDentition,
                            onShowChildDentitionChange = { showChildDentition = it }
                        )
                    }

                    LegendCard()
                }
            }
        }
    }
}

@Composable
fun PortraitOdontogramContent(
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController,
    showChildDentition: Boolean = false,
    onShowChildDentitionChange: (Boolean) -> Unit = {}
) {
    ArcadaVerticalCard(
        title = "Dentadura permanent superior",
        leftTeeth = upperPermanentLeft,
        rightTeeth = upperPermanentRight,
        odontograms = odontograms,
        patientId = patientId,
        navController = navController
    )

    ArcadaVerticalCard(
        title = "Dentadura permanent inferior",
        leftTeeth = lowerPermanentLeft,
        rightTeeth = lowerPermanentRight,
        odontograms = odontograms,
        patientId = patientId,
        navController = navController
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowChildDentitionChange(!showChildDentition) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dentadura temporal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (showChildDentition) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (showChildDentition) "Contraer" else "Expandir"
                )
            }

            HorizontalDivider()

            if (showChildDentition) {
                ArcadaVerticalCardContent(
                    title = "Superior temporal",
                    leftTeeth = upperChildLeft,
                    rightTeeth = upperChildRight,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController
                )

                ArcadaVerticalCardContent(
                    title = "Inferior temporal",
                    leftTeeth = lowerChildLeft,
                    rightTeeth = lowerChildRight,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun LandscapeOdontogramContent(
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController,
    showChildDentition: Boolean = false,
    onShowChildDentitionChange: (Boolean) -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Vista panoràmica",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            ArcadaRowCompact(
                title = "Permanent superior",
                leftTeeth = upperPermanentLeft,
                rightTeeth = upperPermanentRight,
                odontograms = odontograms,
                patientId = patientId,
                navController = navController
            )

            ArcadaRowCompact(
                title = "Permanent inferior",
                leftTeeth = lowerPermanentLeft,
                rightTeeth = lowerPermanentRight,
                odontograms = odontograms,
                patientId = patientId,
                navController = navController
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShowChildDentitionChange(!showChildDentition) },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dentadura temporal",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (showChildDentition) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (showChildDentition) "Contraer" else "Expandir"
                )
            }

            if (showChildDentition) {
                ArcadaRowCompact(
                    title = "Temporal superior",
                    leftTeeth = upperChildLeft,
                    rightTeeth = upperChildRight,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController
                )

                ArcadaRowCompact(
                    title = "Temporal inferior",
                    leftTeeth = lowerChildLeft,
                    rightTeeth = lowerChildRight,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun ArcadaVerticalCard(
    title: String,
    leftTeeth: List<ToothCatalogItem>,
    rightTeeth: List<ToothCatalogItem>,
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        ArcadaVerticalCardContent(
            title = title,
            leftTeeth = leftTeeth,
            rightTeeth = rightTeeth,
            odontograms = odontograms,
            patientId = patientId,
            navController = navController
        )
    }
}

@Composable
fun ArcadaVerticalCardContent(
    title: String,
    leftTeeth: List<ToothCatalogItem>,
    rightTeeth: List<ToothCatalogItem>,
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ToothVerticalColumn(
                teeth = leftTeeth,
                odontograms = odontograms,
                patientId = patientId,
                navController = navController
            )

            ToothVerticalColumn(
                teeth = rightTeeth,
                odontograms = odontograms,
                patientId = patientId,
                navController = navController
            )
        }
    }
}

@Composable
fun ArcadaRowCompact(
    title: String,
    leftTeeth: List<ToothCatalogItem>,
    rightTeeth: List<ToothCatalogItem>,
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val totalGap = 28.dp
            val availableForTwoGroups = maxWidth - totalGap
            val groupWidth = availableForTwoGroups / 2
            val calculatedToothSize = (groupWidth / 8) - 5.dp
            val toothSize = calculatedToothSize.coerceIn(26.dp, 40.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ToothHorizontalRow(
                    teeth = leftTeeth,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController,
                    toothSize = toothSize
                )

                ToothHorizontalRow(
                    teeth = rightTeeth,
                    odontograms = odontograms,
                    patientId = patientId,
                    navController = navController,
                    toothSize = toothSize
                )
            }
        }
    }
}

@Composable
fun ToothVerticalColumn(
    teeth: List<ToothCatalogItem>,
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        teeth.forEach { tooth ->
            val toothRecords = odontograms.filter { it.tooth?.id == tooth.id }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tooth.number.toString(),
                    modifier = Modifier.width(30.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                ToothMiniShape(
                    toothNumber = tooth.number,
                    toothRecords = toothRecords,
                    onClick = { navController.navigate("toothDetail/$patientId/${tooth.id}") }
                )
            }
        }
    }
}

@Composable
fun ToothHorizontalRow(
    teeth: List<ToothCatalogItem>,
    odontograms: List<Odontogram>,
    patientId: Long,
    navController: NavController,
    toothSize: Dp
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        teeth.forEach { tooth ->
            val toothRecords = odontograms.filter { it.tooth?.id == tooth.id }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = tooth.number.toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                ToothMiniShape(
                    toothNumber = tooth.number,
                    toothRecords = toothRecords,
                    onClick = { navController.navigate("toothDetail/$patientId/${tooth.id}") },
                    boxSize = toothSize
                )
            }
        }
    }
}

@Composable
fun ToothMiniShape(
    toothNumber: Int,
    toothRecords: List<Odontogram>,
    onClick: () -> Unit,
    boxSize: Dp = 54.dp
) {
    val hasFiveSides = toothHasFiveSides(toothNumber)
    val wholeToothMissing = isMissingTooth(toothRecords)

    val topData = toothRecords.find { it.side?.id == 1L }
    val leftData = toothRecords.find { it.side?.id == 2L }
    val rightData = toothRecords.find { it.side?.id == 3L }
    val bottomData = toothRecords.find { it.side?.id == 4L }
    val centerData = toothRecords.find { it.side?.id == 5L }

    ToothCanvasDiagram(
        modifier = Modifier
            .size(boxSize)
            .clickable { onClick() },
        hasFiveSides = hasFiveSides,
        topColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(topData),
        leftColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(leftData),
        rightColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(rightData),
        bottomColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(bottomData),
        centerColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(centerData),
        topData = topData,
        leftData = leftData,
        rightData = rightData,
        bottomData = bottomData,
        centerData = centerData
    )
}

@Composable
fun LegendCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Llegenda",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            LegendItem("Patologia o lesió pendent", OdontoRed)
            LegendItem("Tractament fet", OdontoBlue)
            LegendItem("Càries radiogràfica", OdontoGreen)
            LegendItem("Sellat de fosses i fissures", OdontoYellow)
            LegendItem("Absència natural", OdontoBlack)
            LegendItem("Sense registre", OdontoGray)
            LegendItemText("e", "Endodoncia")
            LegendItemText("x", "Extracció")
        }
    }
}

@Composable
fun LegendItem(
    label: String,
    color: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(label)
    }
}

@Composable
fun LegendItemText(
    letter: String,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = letter,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.labelSmall.fontSize
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(label)
    }
}