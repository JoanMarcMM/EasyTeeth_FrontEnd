package com.example.easyteeth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Odontogram
import com.example.easyteeth.model.OdontogramRequest
import com.example.easyteeth.model.Pathology
import com.example.easyteeth.utils.OdontoBlack
import com.example.easyteeth.utils.allTeethCatalog
import com.example.easyteeth.utils.getSimpleOdontogramColor
import com.example.easyteeth.utils.isMissingTooth
import com.example.easyteeth.utils.toothHasFiveSides
import kotlinx.coroutines.launch
import android.content.res.Configuration

@Composable
fun ToothBigDiagram(
    toothNumber: Int,
    sides: List<Odontogram>,
    onClickSide: (Long) -> Unit
) {
    val hasFiveSides = toothHasFiveSides(toothNumber)
    val wholeToothMissing = isMissingTooth(sides)

    val topData = sides.find { it.side?.id == 1L }
    val leftData = sides.find { it.side?.id == 2L }
    val rightData = sides.find { it.side?.id == 3L }
    val bottomData = sides.find { it.side?.id == 4L }
    val centerData = sides.find { it.side?.id == 5L }

    ToothCanvasDiagram(
        modifier = Modifier.size(220.dp),
        hasFiveSides = hasFiveSides,
        topColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(topData),
        leftColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(leftData),
        rightColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(rightData),
        bottomColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(bottomData),
        centerColor = if (wholeToothMissing) OdontoBlack else getSimpleOdontogramColor(centerData),
        enableClicks = true,
        onSideClick = onClickSide,
        showLabels = true,
        topData = topData,
        leftData = leftData,
        rightData = rightData,
        bottomData = bottomData,
        centerData = centerData
    )
}

@Composable
fun ToothLegendItem(label: String) {
    Text(label)
}

// Helper function to check if there are special pathologies (absence, endodontics, extraction)
fun hasSpecialPathologies(sides: List<Odontogram>): Boolean {
    return sides.any { it.pathology?.id in listOf(4L, 5L, 6L) }
}

// Helper function to get only special pathologies
fun getSpecialPathologies(sides: List<Odontogram>): List<Odontogram> {
    return sides.filter { it.pathology?.id in listOf(4L, 5L, 6L) }
}

// Helper function to get normal pathologies (excluding special ones)
fun getNormalPathologies(sides: List<Odontogram>): List<Odontogram> {
    return sides.filter { it.pathology != null && it.pathology?.id !in listOf(4L, 5L, 6L) }
}

// Helper function to check if there's an absence pathology
fun hasAbsence(sides: List<Odontogram>): Boolean {
    return sides.any { it.pathology?.id == 4L }
}

@Composable
fun PathologyListCard(
    sides: List<Odontogram>,
    patientId: Long,
    toothId: Long,
    onRefresh: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }

    // If there are special pathologies, show only those. Otherwise show normal pathologies
    val pathologiesToShow = if (hasSpecialPathologies(sides)) {
        getSpecialPathologies(sides)
    } else {
        getNormalPathologies(sides)
    }

    // Group pathologies with their affected sides
    val pathologyGroups = pathologiesToShow
        .groupBy { it.pathology?.id to it.pathology?.name }

    if (pathologyGroups.isEmpty()) {
        return
    }

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
                text = "Patologies detectades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            pathologyGroups.entries.forEachIndexed { index, (pathologyInfo, records) ->
                val (_, pathologyName) = pathologyInfo
                val affectedSides = records.mapNotNull { it.side?.id }.sorted().joinToString(", ")
                
                // Check if any record is treated
                val allTreated = records.all { it.treated }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pathologyName ?: "Desconeguda",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Cares: $affectedSides",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        // Toggle treatment status
                                        records.forEach { record ->
                                            val request = OdontogramRequest(
                                                patientId = patientId,
                                                toothId = toothId,
                                                sideId = record.side?.id ?: 0L,
                                                pathologyId = record.pathology?.id ?: 0L,
                                                treated = !allTreated,
                                                note = record.note ?: ""
                                            )
                                            RetrofitClient.odontogramApi.update(record.id, request)
                                        }
                                        onRefresh()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isProcessing,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
                        ) {
                            Text(if (allTreated) "No tractat" else "Tractar", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        // Delete all sides with this pathology
                                        records.forEach { record ->
                                            RetrofitClient.odontogramApi.delete(record.id)
                                        }
                                        onRefresh()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            enabled = !isProcessing,
                            border = BorderStroke(2.dp, Color(0xFFD32F2F))
                        ) {
                            Text("Esborrar", color = Color(0xFFD32F2F))
                        }
                    }
                }

                if (index < pathologyGroups.size - 1) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun EditSideDialog(
    existing: Odontogram?,
    patientId: Long,
    toothId: Long,
    sideId: Long,
    allSides: List<Odontogram>,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var pathologyId by remember { mutableStateOf(existing?.pathology?.id ?: 1L) }
    var treated by remember { mutableStateOf(existing?.treated ?: false) }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Check if there's an absence (ID 4) anywhere
    val hasAbsenceAnywhere = hasAbsence(allSides)
    
    // Check if we're currently editing an absence
    val isCurrentlyAbsence = existing?.pathology?.id == 4L

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar costat $sideId") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // Show warning if there's absence and we're not editing it
                if (hasAbsenceAnywhere && !isCurrentlyAbsence) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                        border = BorderStroke(1.dp, Color(0xFFFF9800))
                    ) {
                        Text(
                            text = "⚠ Hi ha absència. Només pots posar o treure absència.",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(8.dp),
                            color = Color(0xFFE65100)
                        )
                    }
                }

                Text("Patologia")

                DropdownSelector(
                    selected = pathologyId,
                    onSelected = { pathologyId = it },
                    restrictedMode = hasAbsenceAnywhere && !isCurrentlyAbsence
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tractat")
                    Switch(
                        checked = treated,
                        onCheckedChange = { treated = it }
                    )
                }

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        try {
                            // If "Ninguna" is selected
                            if (pathologyId == 0L) {
                                // If there's an existing record, delete it
                                if (existing != null) {
                                    val response = RetrofitClient.odontogramApi.delete(existing.id)
                                    if (response.isSuccessful) {
                                        onSaved()
                                    }
                                } else {
                                    // No existing record, just close without saving
                                    onSaved()
                                }
                            } else {
                                // Normal save/update logic
                                val request = OdontogramRequest(
                                    patientId = patientId,
                                    toothId = toothId,
                                    sideId = sideId,
                                    pathologyId = pathologyId,
                                    treated = treated,
                                    note = note
                                )

                                val response = if (existing == null) {
                                    RetrofitClient.odontogramApi.create(request)
                                } else {
                                    RetrofitClient.odontogramApi.update(existing.id, request)
                                }

                                if (response.isSuccessful) {
                                    onSaved()
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Guardar", color = Color.White)
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading,
                border = BorderStroke(2.dp, Color(0xFF1B4B7C))
            ) {
                Text("Cancel·lar", color = Color(0xFF1B4B7C))
            }
        }
    )
}

@Composable
fun DropdownSelector(
    selected: Long,
    onSelected: (Long) -> Unit,
    restrictedMode: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    var pathologies by remember { mutableStateOf<List<Pathology>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val response = RetrofitClient.pathologyApi.getAllPathologies()
                if (response.isSuccessful) {
                    val allPathologies = listOf(
                        Pathology(0L, "Cap")
                    ) + (response.body() ?: emptyList())
                    
                    // If restricted mode is on, only show Ninguna (0) and Absencia (4)
                    pathologies = if (restrictedMode) {
                        allPathologies.filter { it.id in listOf(0L, 4L) }
                    } else {
                        allPathologies
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Box {
        Button(
            onClick = { expanded = true },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4B7C))
        ) {
            Text(pathologies.find { it.id == selected }?.name ?: "Seleccionar", color = Color.White)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            pathologies.forEach { pathology ->
                DropdownMenuItem(
                    text = { Text(pathology.name) },
                    onClick = {
                        onSelected(pathology.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToothDetailScreen(
    navController: NavController,
    patientId: Long,
    toothId: Long
) {
    var sides by remember { mutableStateOf<List<Odontogram>>(emptyList()) }
    var selectedSideId by remember { mutableStateOf<Long?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val toothNumber = allTeethCatalog.firstOrNull { it.id == toothId }?.number ?: toothId.toInt()

    fun loadToothData() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.odontogramApi.getByPatientAndTooth(patientId, toothId)

                if (response.isSuccessful) {
                    sides = response.body() ?: emptyList()
                } else if (response.code() == 404) {
                    sides = emptyList()
                } else {
                    errorMessage = "No s'ha pogut carregar la dent"
                }
                } catch (e: Exception) {
                errorMessage = e.message ?: "Error de connexió"
                } finally {
                isLoading = false
                }
                }
                }

    LaunchedEffect(patientId, toothId) {
        loadToothData()
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dent $toothNumber",
                        fontWeight = FontWeight.SemiBold,
                        style = if (isLandscape) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Tornar"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF7F8FA)
                ),
                modifier = if (isLandscape) Modifier
                    .height(48.dp)
                    .padding(vertical = 0.dp) else Modifier
            )
        }
    ) { innerPadding ->

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left side - Tooth diagram
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    errorMessage != null -> {
                        Box(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = errorMessage ?: "Error",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    else -> {
                        Card(
                            modifier = Modifier
                                .weight(0.4f)
                                .fillMaxHeight(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxHeight(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Editar cares",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                                ToothBigDiagram(
                                    toothNumber = toothNumber,
                                    sides = sides,
                                    onClickSide = { sideId -> selectedSideId = sideId }
                                )
                            }
                        }
                    }
                }

                // Right side - Menu content
                Column(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    when {
                        isLoading -> {}
                        errorMessage != null -> {}
                        else -> {
                            PathologyListCard(
                                sides = sides,
                                patientId = patientId,
                                toothId = toothId,
                                onRefresh = { loadToothData() }
                            )

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Correspondència",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )

                                    HorizontalDivider()

                                    if (toothHasFiveSides(toothNumber)) {
                                        ToothLegendItem("1 · Superior")
                                        ToothLegendItem("2 · Esquerra")
                                        ToothLegendItem("3 · Dreta")
                                        ToothLegendItem("4 · Inferior")
                                        ToothLegendItem("5 · Centre / oclusal")
                                    } else {
                                        ToothLegendItem("1 · Superior")
                                        ToothLegendItem("2 · Esquerra")
                                        ToothLegendItem("3 · Dreta")
                                        ToothLegendItem("4 · Inferior")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Portrait orientation
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
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
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Editar cares de la dent",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                ToothBigDiagram(
                                    toothNumber = toothNumber,
                                    sides = sides,
                                    onClickSide = { sideId -> selectedSideId = sideId }
                                )
                            }
                        }

                        PathologyListCard(
                            sides = sides,
                            patientId = patientId,
                            toothId = toothId,
                            onRefresh = { loadToothData() }
                        )

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Correspondència de cares",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                HorizontalDivider()

                                if (toothHasFiveSides(toothNumber)) {
                                    ToothLegendItem("1 · Superior")
                                    ToothLegendItem("2 · Esquerra")
                                    ToothLegendItem("3 · Dreta")
                                    ToothLegendItem("4 · Inferior")
                                    ToothLegendItem("5 · Centre / oclusal")
                                } else {
                                    ToothLegendItem("1 · Superior")
                                    ToothLegendItem("2 · Esquerra")
                                    ToothLegendItem("3 · Dreta")
                                    ToothLegendItem("4 · Inferior")
                                }
                            }
                        }
                    }
                }
            }
        }

        if (selectedSideId != null) {
            val existing = sides.find { it.side?.id == selectedSideId }

            EditSideDialog(
                existing = existing,
                patientId = patientId,
                toothId = toothId,
                sideId = selectedSideId!!,
                allSides = sides,
                onDismiss = { selectedSideId = null },
                onSaved = {
                    selectedSideId = null
                    loadToothData()
                }
            )
        }
    }
}