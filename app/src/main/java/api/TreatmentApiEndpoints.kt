package api

import com.example.easyteeth.model.Treatment
import com.example.easyteeth.model.Odontologist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface TreatmentApiEndpoints {

    @GET("treatment/index")
    suspend fun getAll(): Response<List<Treatment>>

    @GET("treatment/{id}")
    suspend fun getTreatment(@Path("id") id: Long): Response<Treatment>

    @GET("treatment/{id}/odontologists")
    suspend fun getOdontologistsByTreatment(@Path("id") id: Long?): Response<List<Odontologist>>
}