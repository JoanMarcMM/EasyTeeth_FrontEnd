package screens

import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Image
import com.example.easyteeth.model.ImageRequest
import kotlinx.coroutines.launch
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientImagesScreen(
    navController: NavController,
    patientId: Long
) {
    var images by remember { mutableStateOf<List<Image>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var selectedUploadType by remember { mutableStateOf("normal") }
    var selectedFilter by remember { mutableStateOf("totes") }

    var selectedImageToPreview by remember { mutableStateOf<Image?>(null) }
    var selectedImageToDelete by remember { mutableStateOf<Image?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    fun loadImages() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = RetrofitClient.imageApi.getImagesByPatientId(patientId)
                if (response.isSuccessful) {
                    images = response.body() ?: emptyList()
                } else if (response.code() == 404) {
                    images = emptyList()
                } else {
                    errorMessage = "No s'han pogut carregar les imatges"
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Error de connexió"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(patientId) {
        loadImages()
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
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

                    if (bytes != null) {
                        val response = RetrofitClient.imageApi.createImage(
                            ImageRequest(
                                image = bytes,
                                type = selectedUploadType,
                                patientId = patientId
                            )
                        )

                        if (response.isSuccessful) {
                            loadImages()
                        } else {
                            errorMessage = "Error al pujar la imatge: ${response.code()}"
                        }
                    } else {
                        errorMessage = "No s'ha pogut llegir la imatge"
                    }
                } catch (e: Exception) {
                    errorMessage = e.message ?: "Error en pujar la imatge"
                } finally {
                    isUploading = false
                }
            }
        }
    }

    val filteredImages = remember(images, selectedFilter) {
        when (selectedFilter) {
            "normal" -> images.filter { it.type.equals("normal", ignoreCase = true) }
            "radiografia" -> images.filter { it.type.equals("radiografia", ignoreCase = true) }
            else -> images
        }
    }

    Scaffold(
        containerColor = Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Imatges del pacient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
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
            Text(
                text = "Tipus per a la nova imatge",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedUploadType == "normal",
                    onClick = { selectedUploadType = "normal" },
                    label = { Text("Normal") }
                )

                FilterChip(
                    selected = selectedUploadType == "radiografia",
                    onClick = { selectedUploadType = "radiografia" },
                    label = { Text("Radiografia") }
                )
            }

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = !isUploading
            ) {
                Text(if (isUploading) "Pujant..." else "Pujar imatge")
            }

            Text(
                text = "Filtrar imatges",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "totes",
                    onClick = { selectedFilter = "totes" },
                    label = { Text("Totes") }
                )

                FilterChip(
                    selected = selectedFilter == "normal",
                    onClick = { selectedFilter = "normal" },
                    label = { Text("Normals") }
                )

                FilterChip(
                    selected = selectedFilter == "radiografia",
                    onClick = { selectedFilter = "radiografia" },
                    label = { Text("Radiografies") }
                )
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

                filteredImages.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aquest pacient no té imatges en aquest filtre")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredImages) { imageItem ->
                            ImageCard(
                                imageItem = imageItem,
                                onPreview = { selectedImageToPreview = imageItem },
                                onDelete = { selectedImageToDelete = imageItem },
                                onDownload = {
                                    val success = saveBase64ImageToGallery(
                                        context = context,
                                        base64Image = imageItem.image,
                                        imageType = imageItem.type,
                                        fileName = "patient_${patientId}_image_${imageItem.id ?: System.currentTimeMillis()}"
                                    )

                                    Toast.makeText(
                                        context,
                                        if (success) "Imatge desada al dispositiu" else "No s'ha pogut descarregar la imatge",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (selectedImageToPreview != null) {
        val imageItem = selectedImageToPreview!!

        val bitmap = remember(imageItem.image) {
            try {
                val imageBytes = Base64.decode(imageItem.image, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null
            }
        }

        AlertDialog(
            onDismissRequest = { selectedImageToPreview = null },
            confirmButton = {
                Button(onClick = { selectedImageToPreview = null }) {
                    Text("Tancar")
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tipus: ${imageItem.type}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Vista gran",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(420.dp),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Text("No s'ha pogut mostrar la imatge")
                }
            }
        )
    }

    if (selectedImageToDelete != null) {
        val imageItem = selectedImageToDelete!!

        AlertDialog(
            onDismissRequest = { selectedImageToDelete = null },
            title = { Text("Eliminar imatge") },
            text = { Text("Segur que vols eliminar aquesta imatge?") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            try {
                                imageItem.id?.let { id ->
                                    val response = RetrofitClient.imageApi.deleteImage(id)
                                    if (response.isSuccessful) {
                                        selectedImageToDelete = null
                                        loadImages()
                                    } else {
                                        errorMessage = "Error al eliminar la imatge: ${response.code()}"
                                        selectedImageToDelete = null
                                    }
                                }
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Error eliminant la imatge"
                                selectedImageToDelete = null
                            }
                        }
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { selectedImageToDelete = null }
                ) {
                    Text("Cancel·lar")
                }
            }
        )
    }
}

@Composable
fun ImageCard(
    imageItem: Image,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
    onDownload: () -> Unit
) {
    val bitmap = remember(imageItem.image) {
        try {
            val imageBytes = Base64.decode(imageItem.image, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        } catch (e: Exception) {
            null
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tipus: ${imageItem.type}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Imatge pacient",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clickable { onPreview() },
                    contentScale = ContentScale.Crop
                )
            } ?: Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(Color(0xFFE5E7EB), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No s'ha pogut carregar la imatge")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onPreview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Veure gran")
                }

                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Eliminar")
                }
            }

            OutlinedButton(
                onClick = onDownload,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Descarregar imatge")
            }
        }
    }
}

fun saveBase64ImageToGallery(
    context: android.content.Context,
    base64Image: String,
    imageType: String,
    fileName: String
): Boolean {
    return try {
        val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return false

        val mimeType = when {
            imageType.contains("png", ignoreCase = true) -> "image/png"
            else -> "image/jpeg"
        }

        val extension = if (mimeType == "image/png") ".png" else ".jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName + extension)
            put(MediaStore.Images.Media.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/EasyTeeth"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        val outputStream: OutputStream = resolver.openOutputStream(uri) ?: return false

        if (mimeType == "image/png") {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        } else {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)
        }

        outputStream.flush()
        outputStream.close()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val finalValues = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            resolver.update(uri, finalValues, null, null)
        }

        true
    } catch (e: Exception) {
        false
    }
}