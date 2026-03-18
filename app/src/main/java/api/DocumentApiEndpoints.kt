package api

import com.example.easyteeth.model.Document
import com.example.easyteeth.model.DocumentRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DocumentApiEndpoints {

    @GET("document/patientId/{patientId}")
    suspend fun getDocumentsByPatientId(
        @Path("patientId") patientId: Long
    ): Response<List<Document>>

    @POST("document/new")
    suspend fun createDocument(
        @Body request: DocumentRequest
    ): Response<Document>

    @DELETE("document/{id}")
    suspend fun deleteDocument(
        @Path("id") id: Long
    ): Response<String>
}