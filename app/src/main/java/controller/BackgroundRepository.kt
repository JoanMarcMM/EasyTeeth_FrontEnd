package controller

import com.example.easyteeth.api.BackgroundApiEndpoints
import com.example.easyteeth.model.Background
import com.example.easyteeth.model.BackgroundRequest
import retrofit2.Response

class BackgroundRepository(
    private val api: BackgroundApiEndpoints
) {

    suspend fun createBackground(request: BackgroundRequest): Response<Background> {
        return api.createBackground(request)
    }

    suspend fun getBackgroundById(id: Long): Response<Background> {
        return api.getBackgroundById(id)
    }

    suspend fun getAllBackgrounds(): Response<List<Background>> {
        return api.getAllBackgrounds()
    }

    suspend fun getBackgroundsByPatientId(patientId: Long): Response<List<Background>> {
        return api.getBackgroundsByPatientId(patientId)
    }

    suspend fun updateBackground(id: Long, request: BackgroundRequest): Response<Background> {
        return api.updateBackground(id, request)
    }

    suspend fun deleteBackground(id: Long): Response<String> {
        return api.deleteBackground(id)
    }
}