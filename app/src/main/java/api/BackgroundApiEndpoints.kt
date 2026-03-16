package com.example.easyteeth.api

import com.example.easyteeth.model.Background
import com.example.easyteeth.model.BackgroundRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BackgroundApiEndpoints {

    @POST("background/new")
    suspend fun createBackground(
        @Body backgroundRequest: BackgroundRequest
    ): Response<Background>

    @GET("background/{id}")
    suspend fun getBackgroundById(
        @Path("id") id: Long
    ): Response<Background>

    @DELETE("background/{id}")
    suspend fun deleteBackground(
        @Path("id") id: Long
    ): Response<String>

    @GET("background/index")
    suspend fun getAllBackgrounds(): Response<List<Background>>

    @PUT("background/{id}")
    suspend fun updateBackground(
        @Path("id") id: Long,
        @Body backgroundRequest: BackgroundRequest
    ): Response<Background>

    @GET("background/patientId/{patientId}")
    suspend fun getBackgroundsByPatientId(
        @Path("patientId") patientId: Long
    ): Response<List<Background>>
}