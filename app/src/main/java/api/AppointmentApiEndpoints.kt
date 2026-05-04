package api
import com.example.easyteeth.model.Appointment
import com.example.easyteeth.model.AppointmentRequest
import com.example.easyteeth.model.AvailableSlotResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface AppointmentApiEndpoints {
    @GET("appointment/dateBetween/{date}")
    suspend fun getAppointmentsByDay(@Path("date") date: String): Response<List<Appointment>>

    @GET("appointment/index")
    suspend fun getAllAppointments(): Response<List<Appointment>>

    @POST("appointment/new")
    suspend fun createAppointment(@Body req: AppointmentRequest): Response<Appointment>

    @DELETE("appointment/{appointmentId}")
    suspend fun deleteAppointment(@Path("appointmentId") appointmentId: Long): Response<Unit>

    @PUT("appointment/{appointmentId}")
    suspend fun updateAppointment(
        @Path("appointmentId") appointmentId: Long,
        @Body req: AppointmentRequest
    ): Response<Appointment>

    @GET("appointment/available-slots/{odontologistId}")
    suspend fun getAvailableSlots(
        @Path("odontologistId") odontologistId: Long,
        @Query("startDate") startDate: String? = null,
        @Query("boxId") boxId: Long? = null
    ): Response<List<AvailableSlotResponse>>
}