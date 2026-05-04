package api

import com.example.easyteeth.model.Pathology
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PathologyApiEndpoints {
    
    @GET("pathology/index")
    suspend fun getAllPathologies(): Response<List<Pathology>>
    @GET("pathology/{id}")
    suspend fun getPathologyById(@Path("id") id: Long): Response<Pathology>
}
