package api

import com.example.easyteeth.model.Box
import com.example.easyteeth.model.StockBox
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BoxApiEndpoints {
    @GET("box/index")
    suspend fun getAllBoxes(): Response<List<Box>>

    @POST("box/{id}/assign-materials")
    suspend fun assignMaterials(
        @Path("id") boxId: Long,
        @Query("date") date: String
    ): Response<Unit>
    @GET("box/{id}/materials")
    suspend fun getMaterialsByDay(
        @Path("id") boxId: Long,
        @Query("date") date: String
    ): Response<List<StockBox>>

    @POST("box/{id}/update-status")
    suspend fun updateStockStatus(
        @Path("id") boxId: Long,
        @Query("date") date: String,
        @Query("status") status: Boolean
    ): Response<Unit>
}