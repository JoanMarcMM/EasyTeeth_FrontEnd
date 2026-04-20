package com.example.easyteeth.api

import com.example.easyteeth.model.HistoricUtensil
import com.example.easyteeth.model.HistoricUtensilRequest
import retrofit2.Response
import retrofit2.http.*

interface HistoricUtensilApiEndpoints {
    @POST("historicUtensil/new")
    suspend fun createHistoricUtensil(@Body request: HistoricUtensilRequest): Response<HistoricUtensil>

    @GET("historicUtensil/index")
    suspend fun getAll(): Response<List<HistoricUtensil>>

    @GET("historicUtensil/{id}")
    suspend fun getHistoricUtensilById(@Path("id") id: Long): Response<HistoricUtensil>
}
