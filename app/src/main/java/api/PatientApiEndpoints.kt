package api
import com.example.easyteeth.model.Patient


/*interface PatientApiEndpoints {

    @POST("patient/new")
    suspend fun createPatient(
        @Body patient: Patient
    ): Response<Patient>

    @GET("patient/{id}")
    suspend fun getPatientById(
        @Path("id") id: Long
    ): Response<Patient>

    @DELETE("patient/{id}")
    suspend fun deletePatient(
        @Path("id") id: Long
    ): Response<ResponseBody>

    @GET("patient/index")
    suspend fun getAllPatients(): Response<List<Patient>>

    @PUT("patient/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body patient: Patient
    ): Response<Any> // Usamos Any porque el backend puede devolver el Patient o una lista de errores (String)

    @GET("patient/name/{name}")
    suspend fun findByName(
        @Path("name") name: String
    ): Response<List<Patient>>

    @GET("patient/dni/{dni}")
    suspend fun findByDni(
        @Path("dni") dni: String
    ): Response<List<Patient>>

    @GET("patient/ssn/{ssn}")
    suspend fun findBySsn(
        @Path("ssn") ssn: String
    ): Response<List<Patient>>
}*/