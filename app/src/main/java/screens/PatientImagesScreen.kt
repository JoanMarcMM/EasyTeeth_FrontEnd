package screens
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import api.RetrofitClient
import com.example.easyteeth.model.Image
import com.example.easyteeth.model.ImageRequest
import kotlinx.coroutines.launch

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
                        val mimeType = context.contentResolver.getType(uri) ?: "image/*"

                        val response = RetrofitClient.imageApi.createImage(
                            ImageRequest(
                                image = bytes,
                                type = mimeType,
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

    Scaffold(
        containerColor = androidx.compose.ui.graphics.Color(0xFFF7F8FA),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Imatges del pacient") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Tornar")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = androidx.compose.ui.graphics.Color(0xFFF7F8FA)
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
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                enabled = !isUploading
            ) {
                Text(if (isUploading) "Pujant..." else "Pujar imatge")
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

                images.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Aquest pacient no té imatges")
                    }
                }

                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(images) { imageItem ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = androidx.compose.ui.graphics.Color.White
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "Tipus: ${imageItem.type}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    val bitmap = remember(imageItem.image) {
                                        try {
                                            val imageBytes = Base64.decode(imageItem.image, Base64.DEFAULT)
                                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                                        } catch (e: Exception) {
                                            null
                                        }
                                    }

                                    bitmap?.let {
                                        Image(
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = "Imatge pacient",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(220.dp)
                                        )
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                imageItem.id?.let { id ->
                                                    RetrofitClient.imageApi.deleteImage(id)
                                                    loadImages()
                                                }
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Eliminar imatge")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}