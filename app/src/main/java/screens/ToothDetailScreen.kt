package com.example.easyteeth.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Odontogram
import com.example.easyteeth.model.OdontogramRequest
import com.example.easyteeth.utils.OdontoBlack
import com.example.easyteeth.utils.allTeethCatalog
import com.example.easyteeth.utils.getSimpleOdontogramColor
import com.example.easyteeth.utils.isMissingTooth
import com.example.easyteeth.utils.toothHasFiveSides
import kotlinx.coroutines.launch

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
                    errorMessage = "No s'ha pogut carregar el dent"
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
                        fontWeight = FontWeight.SemiBold
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
                )
            )
        }
    ) { innerPadding ->

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
                                text = "Editar cares del dent",
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

        if (selectedSideId != null) {
            val existing = sides.find { it.side?.id == selectedSideId }

            EditSideDialog(
                existing = existing,
                patientId = patientId,
                toothId = toothId,
                sideId = selectedSideId!!,
                onDismiss = { selectedSideId = null },
                onSaved = {
                    selectedSideId = null
                    loadToothData()
                }
            )
        }
    }
}

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
        showLabels = true
    )
}

@Composable
fun EditSideDialog(
    existing: Odontogram?,
    patientId: Long,
    toothId: Long,
    sideId: Long,
    onDismiss: () -> Unit,
    onSaved: () -> Unit
) {
    var pathologyId by remember { mutableStateOf(existing?.pathology?.id ?: 1L) }
    var treated by remember { mutableStateOf(existing?.treated ?: false) }
    var note by remember { mutableStateOf(existing?.note ?: "") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar costat $sideId") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Patologia")

                DropdownSelector(
                    selected = pathologyId,
                    onSelected = { pathologyId = it }
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
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel·lar")
            }
        }
    )
}

@Composable
fun DropdownSelector(
    selected: Long,
    onSelected: (Long) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val options = listOf(
        1L to "Càries oclusal",
        2L to "Càries proximal",
        3L to "Càries cervical",
        4L to "Fractura dental",
        5L to "Desgast oclusal",
        6L to "Lesió periapical",
        7L to "Pulpitis",
        8L to "Abscés",
        9L to "Càries radiogràfica",
        10L to "Sellat de fosses i fissures",
        11L to "Absència natural"
    )

    Box {
        Button(onClick = { expanded = true }) {
            Text(options.find { it.first == selected }?.second ?: "Seleccionar")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.second) },
                    onClick = {
                        onSelected(option.first)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ToothLegendItem(label: String) {
    Text(label)
}