package api

import model.Appointment

/*interface AppointmentApiEndpoints {

    @POST("appointment/new")
    suspend fun createAppointment(
        @Body request: AppointmentRequest
    ): Response<Appointment>

    @GET("appointment/{id}")
    suspend fun getAppointment(
        @Path("id") id: Long
    ): Response<Appointment>

    @DELETE("appointment/{id}")
    suspend fun deleteAppointment(
        @Path("id") id: Long
    ): Response<ResponseBody>

    @GET("appointment/index")
    suspend fun getAllAppointments(): Response<List<Appointment>>

    @PUT("appointment/{id}")
    suspend fun updateAppointment(
        @Path("id") id: Long,
        @Body request: AppointmentRequest
    ): Response<Appointment>

    @GET("appointment/date/{date}")
    suspend fun findByDate(
        @Path("date") date: String // O LocalDateTime si tienes configurado un convertidor
    ): Response<List<Appointment>>

    @GET("appointment/dateBetween/{date}")
    suspend fun findByDay(
        @Path("date") date: String // Formato ISO "yyyy-MM-dd"
    ): Response<List<Appointment>>

    @GET("appointment/patientId/{patientId}")
    suspend fun findByPatientId(
        @Path("patientId") patientId: Long
    ): Response<List<Appointment>>

    @GET("appointment/boxId/{boxId}")
    suspend fun findByBoxId(
        @Path("boxId") boxId: Long
    ): Response<List<Appointment>>

    @GET("appointment/odontologistId/{odontologistId}")
    suspend fun findByOdontologistId(
        @Path("odontologistId") odontologistId: Long
    ): Response<List<Appointment>>

    @GET("appointment/treatmentId/{treatmentId}")
    suspend fun findByTreatmentId(
        @Path("treatmentId") treatmentId: Long
    ): Response<List<Appointment>>
}*/