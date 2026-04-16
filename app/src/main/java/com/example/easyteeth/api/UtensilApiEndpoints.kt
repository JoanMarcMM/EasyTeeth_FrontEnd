package com.example.easyteeth.api

import com.example.easyteeth.model.Utensil
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface UtensilApiEndpoints {
    @GET("utensil/index")
    suspend fun getAll(): Response<List<Utensil>>

    @GET("utensil/{id}")
    suspend fun getUtensilById(@Path("id") id: Long): Response<Utensil>
}
