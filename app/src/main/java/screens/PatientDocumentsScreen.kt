package screens

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Document
import com.example.easyteeth.model.DocumentRequest
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
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

                    val fileName = extractSafeFileName(context, uri)
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
                                },
                                onDownload = {
                                    val success = saveBase64DocumentToDownloads(
                                        context = context,
                                        base64File = documentItem.file,
                                        fileName = documentItem.name,
                                        mimeType = documentItem.type
                                    )

                                    Toast.makeText(
                                        context,
                                        if (success) "Document desat a Descàrregues" else "No s'ha pogut descarregar el document",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                onView = {
                                    val success = openBase64Document(
                                        context = context,
                                        base64File = documentItem.file,
                                        fileName = documentItem.name,
                                        mimeType = documentItem.type
                                    )

                                    if (!success) {
                                        Toast.makeText(
                                            context,
                                            "No s'ha pogut obrir el document",
                                            Toast.LENGTH_SHORT
                                        ).show()
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
    onDelete: () -> Unit,
    onDownload: () -> Unit,
    onView: () -> Unit
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
                    Base64.decode(document.file, Base64.DEFAULT).size.toLong()
                } catch (e: Exception) {
                    0L
                }
            }

            DocumentInfoRow(
                "Tamany",
                formatFileSize(fileSizeBytes)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Veure")
                }

                OutlinedButton(
                    onClick = onDownload,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Descarregar")
                }
            }

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

fun extractSafeFileName(context: Context, uri: Uri): String {
    val rawName = uri.lastPathSegment?.substringAfterLast('/') ?: "document"
    return if (rawName.contains(".")) rawName else "$rawName.bin"
}

fun saveBase64DocumentToDownloads(
    context: Context,
    base64File: String,
    fileName: String,
    mimeType: String
): Boolean {
    return try {
        val fileBytes = Base64.decode(base64File, Base64.DEFAULT)

        val safeName = if (fileName.contains(".")) {
            fileName
        } else {
            fileName + extensionFromMimeType(mimeType)
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, safeName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/EasyTeeth")
                put(MediaStore.Downloads.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        val outputStream: OutputStream = resolver.openOutputStream(uri) ?: return false
        outputStream.write(fileBytes)
        outputStream.flush()
        outputStream.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val finalValues = ContentValues().apply {
                put(MediaStore.Downloads.IS_PENDING, 0)
            }
            resolver.update(uri, finalValues, null, null)
        }

        true
    } catch (e: Exception) {
        false
    }
}

fun openBase64Document(
    context: Context,
    base64File: String,
    fileName: String,
    mimeType: String
): Boolean {
    return try {
        val fileBytes = Base64.decode(base64File, Base64.DEFAULT)

        val safeName = if (fileName.contains(".")) {
            fileName
        } else {
            fileName + extensionFromMimeType(mimeType)
        }

        val tempFile = File(context.cacheDir, safeName)
        val fos = FileOutputStream(tempFile)
        fos.write(fileBytes)
        fos.flush()
        fos.close()

        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, mimeType)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
        true
    } catch (e: Exception) {
        false
    }
}

fun extensionFromMimeType(mimeType: String): String {
    return when (mimeType.lowercase()) {
        "application/pdf" -> ".pdf"
        "application/msword" -> ".doc"
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> ".docx"
        "application/vnd.ms-excel" -> ".xls"
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> ".xlsx"
        "text/plain" -> ".txt"
        "image/png" -> ".png"
        "image/jpeg" -> ".jpg"
        else -> ".bin"
    }
}