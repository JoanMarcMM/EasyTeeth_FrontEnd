package api

import com.example.easyteeth.model.Odontologist
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface OdontologistApiEndpoints {

    @GET("odontologist/index")
    suspend fun getAll(): Response<List<Odontologist>>

    @GET("odontologist/{id}")
    suspend fun getOdontologist(@Path("id") id: Long): Response<Odontologist>

    @GET("odontologist/specialityId/{specialityId}")
    suspend fun findBySpeciality(@Path("specialityId") specialityId: Long): Response<List<Odontologist>>
}