package api

import com.example.easyteeth.model.Image
import com.example.easyteeth.model.ImageRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ImageApiEndpoints {

    @GET("image/patientId/{patientId}")
    suspend fun getImagesByPatientId(
        @Path("patientId") patientId: Long
    ): Response<List<Image>>

    @POST("image/new")
    suspend fun createImage(
        @Body request: ImageRequest
    ): Response<Image>

    @DELETE("image/{id}")
    suspend fun deleteImage(
        @Path("id") id: Long
    ): Response<String>
}