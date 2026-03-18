package screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Document
import com.example.easyteeth.model.DocumentRequest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

private const val MAX_DOCUMENT_SIZE_BYTES = 1_000_000L // ~1 MB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientDocumentsScreen(
    navController: NavController,
    patientId: Long
) {
    var documents by remember { mutableStateOf<List<Document>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun loadDocuments() {
        scope.launch {
            isLoading = true
            errorMessage = null

            try {
                val response = RetrofitClient.documentApi.getDocumentsByPatientId(patientId)

                if (response.isSuccessful) {
                    documents = response.body() ?: emptyList()
                } else if (response.code() == 404) {
                    documents = emptyList()
                } else {
                    errorMessage = "No s'han pogut carregar els documents"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error de connexió"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(patientId) {
        loadDocuments()
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                isUploading = true
                errorMessage = null

                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes == null || bytes.isEmpty()) {
                        errorMessage = "No s'ha pogut llegir el document"
                        isUploading = false
                        return@launch
                    }

                    if (bytes.size.toLong() > MAX_DOCUMENT_SIZE_BYTES) {
                        errorMessage =
                            "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"
                        isUploading = false
                        return@launch
                    }

                    val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "document"
                    val mimeType = context.contentResolver.getType(uri) ?: "application/octet-stream"

                    val response = RetrofitClient.documentApi.createDocument(
                        DocumentRequest(
                            name = fileName,
                            type = mimeType,
                            file = bytes,
                            creationDate = LocalDateTime.now().toString(),
                            patientId = patientId
                        )
                    )

                    if (response.isSuccessful) {
                        loadDocuments()
                    } else {
                        val serverError = try {
                            response.errorBody()?.string().orEmpty()
                        } catch (e: Exception) {
                            ""
                        }

                        errorMessage = when {
                            response.code() == 413 ->
                                "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                            serverError.contains("PacketTooBigException", ignoreCase = true) ->
                                "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                            serverError.contains("max_allowed_packet", ignoreCase = true) ->
                                "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                            serverError.contains("too large", ignoreCase = true) ->
                                "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                            else ->
                                "Error al guardar el document: ${response.code()}"
                        }
                    }
                } catch (e: Exception) {
                    errorMessage = when {
                        e.message?.contains("PacketTooBigException", ignoreCase = true) == true ->
                            "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                        e.message?.contains("max_allowed_packet", ignoreCase = true) == true ->
                            "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                        e.message?.contains("too large", ignoreCase = true) == true ->
                            "Arxiu massa gran. La mida màxima és ${formatFileSize(MAX_DOCUMENT_SIZE_BYTES)}"

                        else ->
                            e.message ?: "Error en pujar el document"
                    }
                } finally {
                    isUploading = false
                }
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Documents del pacient",
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { documentPickerLauncher.launch("*/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator()
                } else {
                    Text("Afegir document")
                }
            }

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                documents.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aquest pacient no té documents")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(documents) { documentItem ->
                            DocumentCard(
                                document = documentItem,
                                onDelete = {
                                    scope.launch {
                                        try {
                                            documentItem.id?.let { id ->
                                                RetrofitClient.documentApi.deleteDocument(id)
                                                loadDocuments()
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = e.message ?: "Error eliminant el document"
                                        }
                                    }
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Tornar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DocumentCard(
    document: Document,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = document.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            HorizontalDivider()

            DocumentInfoRow("Tipus", document.type)
            DocumentInfoRow("Data de creació", document.creationDate)

            val fileSizeBytes = remember(document.file) {
                try {
                    android.util.Base64.decode(document.file, android.util.Base64.DEFAULT).size.toLong()
                } catch (e: Exception) {
                    0L
                }
            }

            DocumentInfoRow(
                "Tamany",
                formatFileSize(fileSizeBytes)
            )

            OutlinedButton(
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Eliminar document")
            }
        }
    }
}

@Composable
fun DocumentInfoRow(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color(0xFF6B7280),
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value.ifBlank { "-" },
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF111827)
        )
    }
}

fun formatFileSize(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> String.format("%.1f KB", bytes / 1024.0)
        else -> String.format("%.1f MB", bytes / (1024.0 * 1024.0))
    }
}