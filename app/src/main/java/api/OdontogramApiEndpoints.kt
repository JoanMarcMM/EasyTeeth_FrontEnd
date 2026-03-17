package api

import com.example.easyteeth.model.Odontogram
import com.example.easyteeth.model.OdontogramRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OdontogramApiEndpoints {

    @GET("odontogram/patient/{patientId}")
    suspend fun getByPatient(
        @Path("patientId") patientId: Long
    ): Response<List<Odontogram>>

    @GET("odontogram/patient/{patientId}/tooth/{toothId}")
    suspend fun getByPatientAndTooth(
        @Path("patientId") patientId: Long,
        @Path("toothId") toothId: Long
    ): Response<List<Odontogram>>

    @POST("odontogram/new")
    suspend fun create(
        @Body request: OdontogramRequest
    ): Response<Odontogram>

    @PUT("odontogram/{id}")
    suspend fun update(
        @Path("id") id: Long,
        @Body request: OdontogramRequest
    ): Response<Odontogram>
}