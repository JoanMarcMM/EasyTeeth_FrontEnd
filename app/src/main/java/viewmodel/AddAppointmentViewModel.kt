package viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import api.RetrofitClient
import api.TreatmentApiEndpoints
import api.OdontologistApiEndpoints
import api.BoxApiEndpoints
import api.OdontogramApiEndpoints
import api.PathologyApiEndpoints
import com.example.easyteeth.model.Pathology
import com.example.easyteeth.model.Treatment
import com.example.easyteeth.model.Odontologist
import com.example.easyteeth.model.Box
import kotlinx.coroutines.launch

class AddAppointmentViewModel : ViewModel() {
    private val odontogramApi = RetrofitClient.instance.create(OdontogramApiEndpoints::class.java)
    private val pathologyApi = RetrofitClient.instance.create(PathologyApiEndpoints::class.java)
    private val treatmentApi = RetrofitClient.instance.create(TreatmentApiEndpoints::class.java)
    private val odontologistApi = RetrofitClient.instance.create(OdontologistApiEndpoints::class.java)
    private val boxApi = RetrofitClient.instance.create(BoxApiEndpoints::class.java)

    // Estados de la pantalla de detalles de cita
    var patientId by mutableStateOf<Long?>(null)
    var isLoadingPathologies by mutableStateOf(false)
    var isLoadingTreatments by mutableStateOf(false)
    var isLoadingOdontologists by mutableStateOf(false)
    var isLoadingBoxes by mutableStateOf(false)
    var isLoadingPathologyTreatments by mutableStateOf(false)

    var errorMessage by mutableStateOf<String?>(null)

    // Datos: Patologías únicas del paciente
    var patientPathologies = mutableStateListOf<Pathology>()

    // Datos: Todos los tratamientos disponibles
    var allTreatments = mutableStateListOf<Treatment>()

    // Datos: Tratamientos filtrados para la patología seleccionada
    var filteredTreatments = mutableStateListOf<Treatment>()

    // Datos: Odontólogos disponibles
    var availableOdontologists = mutableStateListOf<Odontologist>()

    // Datos: Cajas disponibles
    var availableBoxes = mutableStateListOf<Box>()

    // Selecciones del usuario
    var selectedPathology by mutableStateOf<Pathology?>(null)
    var selectedTreatment by mutableStateOf<Treatment?>(null)
    var selectedOdontologist by mutableStateOf<Odontologist?>(null)

    /**
     * Inicializa el ViewModel con el patientId
     * Carga las patologías, odontólogos y cajas disponibles
     * Solo ejecuta si no ha sido inicializado previamente
     */
    fun initialize(patientId: Long) {
        // Solo inicializar si el patientId cambió o es la primera inicialización
        if (this.patientId == patientId) {
            return // Ya fue inicializado con este patientId
        }

        this.patientId = patientId

        viewModelScope.launch {
            // Cargar todo en paralelo
            val pathologiesJob = viewModelScope.launch { loadPatientPathologiesFromOdontogram(patientId) }
            val odontologistsJob = viewModelScope.launch { loadOdontologists() }
            val boxesJob = viewModelScope.launch { loadBoxes() }

            // Esperar a que todas las cargas terminen
            pathologiesJob.join()
            odontologistsJob.join()
            boxesJob.join()
        }
    }

    /**
     * Obtiene las patologías del odontograma del paciente
     */
    private suspend fun loadPatientPathologiesFromOdontogram(patientId: Long) {
        isLoadingPathologies = true
        errorMessage = null

        try {
            // Obtener el odontograma del paciente
            val response = odontogramApi.getByPatient(patientId)

            if (response.isSuccessful) {
                val odontogramList = response.body() ?: emptyList()

                // Extraer todas las patologías únicas del odontograma
                val pathologiesSet = mutableSetOf<Pathology>()

                for (odontogram in odontogramList) {
                    odontogram.pathology?.let { pathology ->
                        pathologiesSet.add(pathology)
                    }
                }

                patientPathologies.clear()
                patientPathologies.addAll(pathologiesSet.toList())

            } else {
                errorMessage = "Error al obtener patologías: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Excepción al cargar patologías: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingPathologies = false
        }
    }

    /**
     * Obtiene todas las cajas disponibles
     */
    private suspend fun loadBoxes() {
        isLoadingBoxes = true

        try {
            val response = boxApi.getAllBoxes()

            if (response.isSuccessful) {
                val boxes = response.body() ?: emptyList()
                availableBoxes.clear()
                availableBoxes.addAll(boxes)
            }
        } catch (e: Exception) {
            errorMessage = "Excepción al cargar cajas: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingBoxes = false
        }
    }

    /**
     * Obtiene todos los odontólogos disponibles
     */
    private suspend fun loadOdontologists() {
        isLoadingOdontologists = true

        try {
            val response = odontologistApi.getAll()

            if (response.isSuccessful) {
                val odontologists = response.body() ?: emptyList()
                availableOdontologists.clear()
                availableOdontologists.addAll(odontologists)
            }
        } catch (e: Exception) {
            errorMessage = "Excepción al cargar odontólogos: ${e.message}"
            e.printStackTrace()
        } finally {
            isLoadingOdontologists = false
        }
    }

    /**
     * Obtiene los tratamientos asociados a una patología desde el backend
     * Usa la relación pathology_treatment
     * @param pathology Patología seleccionada
     */
    fun onPathologySelected(pathology: Pathology) {
        selectedPathology = pathology
        selectedTreatment = null // Limpiar selección de tratamiento anterior

        viewModelScope.launch {
            isLoadingPathologyTreatments = true
            errorMessage = null

            try {
                // Obtener la patología completa con sus tratamientos asociados
                val response = pathologyApi.getPathologyById(pathology.id)

                if (response.isSuccessful) {
                    val pathologyWithTreatments = response.body()

                    if (pathologyWithTreatments != null && pathologyWithTreatments.treatments != null) {
                        // Convertir Set<Treatment> a List y mostrar
                        filteredTreatments.clear()
                        filteredTreatments.addAll(pathologyWithTreatments.treatments.toList())
                    } else {
                        errorMessage = "No hay tratamientos disponibles para esta patología"
                        filteredTreatments.clear()
                    }
                } else {
                    errorMessage = "Error al obtener tratamientos: ${response.code()}"
                    filteredTreatments.clear()
                }
            } catch (e: Exception) {
                errorMessage = "Excepción al cargar tratamientos: ${e.message}"
                filteredTreatments.clear()
                e.printStackTrace()
            } finally {
                isLoadingPathologyTreatments = false
            }
        }
    }

    /**
     * Selecciona un tratamiento y carga odontólogos especializados
     * @param treatment Tratamiento seleccionado
     */
    fun onTreatmentSelected(treatment: Treatment) {
        selectedOdontologist = null // Limpiar selección anterior

        viewModelScope.launch {
            isLoadingOdontologists = true
            errorMessage = null

            try {
                // Obtener odontólogos especializados en este tratamiento
                val odontologistsResponse = treatmentApi.getOdontologistsByTreatment(treatment.id)

                if (odontologistsResponse.isSuccessful) {
                    val odontologists = odontologistsResponse.body() ?: emptyList()

                    if (odontologists.isEmpty()) {
                        errorMessage = "No hay odontólogos disponibles para este tratamiento"
                    }

                    selectedTreatment = treatment
                    availableOdontologists.clear()
                    availableOdontologists.addAll(odontologists)
                } else {
                    errorMessage = "Error al obtener odontólogos: ${odontologistsResponse.code()}"
                    availableOdontologists.clear()
                }
            } catch (e: Exception) {
                errorMessage = "Excepción al cargar odontólogos: ${e.message}"
                availableOdontologists.clear()
                e.printStackTrace()
            } finally {
                isLoadingOdontologists = false
            }
        }
    }

    /**
     * Selecciona un odontólogo
     * @param odontologist Odontólogo seleccionado
     */
    fun onOdontologistSelected(odontologist: Odontologist) {
        selectedOdontologist = odontologist
    }

    /**
     * Verifica si todo está listo para proceder
     */
    fun isReadyToProceed(): Boolean {
        return selectedPathology != null && selectedTreatment != null && selectedOdontologist != null
    }
}
