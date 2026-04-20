package com.example.easyteeth.api

import com.example.easyteeth.model.Utensil
import com.example.easyteeth.model.UtensilRequest
import retrofit2.Response
import retrofit2.http.*

interface UtensilApiEndpoints {
    @GET("utensil/index")
    suspend fun getAll(): Response<List<Utensil>>

    @GET("utensil/{id}")
    suspend fun getUtensilById(@Path("id") id: Long): Response<Utensil>

    @POST("utensil/new")
    suspend fun createUtensil(@Body request: UtensilRequest): Response<Utensil>

    @PUT("utensil/{id}")
    suspend fun updateUtensil(@Path("id") id: Long, @Body request: UtensilRequest): Response<Utensil>

    @DELETE("utensil/{id}")
    suspend fun deleteUtensil(@Path("id") id: Long): Response<String>
}
