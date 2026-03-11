package api
import com.example.easyteeth.model.Appointment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path


interface AppointmentApiEndpoints {
    @GET("appointment/dateBetween/{date}")
    suspend fun getAppointmentsByDay(@Path("date") date: String): Response<List<Appointment>>
}