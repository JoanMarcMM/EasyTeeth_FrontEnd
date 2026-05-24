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
import com.example.easyteeth.api.BackgroundApiEndpoints
import com.example.easyteeth.api.PatientApiEndpoints
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

    private val patientApi = RetrofitClient.instance.create(PatientApiEndpoints::class.java)
    private val backgroundApi = RetrofitClient.instance.create(BackgroundApiEndpoints::class.java)

    // Estados de la pantalla de detalles de cita
    var patientId by mutableStateOf<Long?>(null)
    var hasMedicalAlert by mutableStateOf(false)
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
            val alertsJob = viewModelScope.launch { checkMedicalAlerts(patientId) }

            // Esperar a que todas las cargas terminen
            pathologiesJob.join()
            odontologistsJob.join()
            boxesJob.join()
            alertsJob.join()
        }
    }

    /**
     * Verifica si el paciente tiene enfermedades contagiosas o alergias importantes
     */
    private suspend fun checkMedicalAlerts(patientId: Long) {
        try {
            // 1. Verificar datos básicos del paciente
            val patientResponse = patientApi.getPatientById(patientId)
            if (patientResponse.isSuccessful) {
                val patient = patientResponse.body()
                if (patient?.isContagious == true || patient?.hasAllergies == true) {
                    hasMedicalAlert = true
                    return
                }
            }

            // 2. Verificar el background clínico
            val backgroundResponse = backgroundApi.getBackgroundsByPatientId(patientId)
            if (backgroundResponse.isSuccessful) {
                val backgrounds = backgroundResponse.body() ?: emptyList()
                if (backgrounds.any { it.infectiousDisease || it.importantAllergie }) {
                    hasMedicalAlert = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
                
                if (patientPathologies.isEmpty()) {
                    errorMessage = "El pacient no té patologies, assigneu-li una."
                }

            } else if (response.code() == 404) {
                errorMessage = "El pacient no té patologies, assigneu-li una."
            } else {
                errorMessage = "Error en obtenir les patologies: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Excepció en carregar les patologies: ${e.message}"
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
            errorMessage = "Excepció en carregar els boxes: ${e.message}"
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
            errorMessage = "Excepció en carregar els odontòlegs: ${e.message}"
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
                        
                        if (filteredTreatments.isEmpty()) {
                            errorMessage = "No hi ha tractaments disponibles per a aquesta patologia"
                        }
                    } else {
                        errorMessage = "No hi ha tractaments disponibles per a aquesta patologia"
                        filteredTreatments.clear()
                    }
                } else {
                    errorMessage = "Error en obtenir els tractaments: ${response.code()}"
                    filteredTreatments.clear()
                }
            } catch (e: Exception) {
                errorMessage = "Excepció en carregar els tractaments: ${e.message}"
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
                        errorMessage = "No hi ha odontòlegs disponibles per a aquest tractament"
                    }

                    selectedTreatment = treatment
                    availableOdontologists.clear()
                    availableOdontologists.addAll(odontologists)
                } else {
                    errorMessage = "Error en obtenir els odontòlegs: ${odontologistsResponse.code()}"
                    availableOdontologists.clear()
                }
            } catch (e: Exception) {
                errorMessage = "Excepció en carregar els odontòlegs: ${e.message}"
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
