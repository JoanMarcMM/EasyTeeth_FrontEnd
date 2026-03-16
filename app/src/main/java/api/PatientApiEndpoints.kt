package com.example.easyteeth.api

import com.example.easyteeth.model.Patient
import com.example.easyteeth.model.PatientRequest

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

interface PatientApiEndpoints {

    @POST("patient/new")
    suspend fun createPatient(
        @Body request: PatientRequest
    ): Response<Patient>

    @GET("patient/{id}")
    suspend fun getPatientById(
        @Path("id") id: Long
    ): Response<Patient>

    @GET("patient/index")
    suspend fun getAllPatients(): Response<List<Patient>>

    @PUT("patient/{id}")
    suspend fun updatePatient(
        @Path("id") id: Long,
        @Body request: PatientRequest
    ): Response<Patient>

    @DELETE("patient/{id}")
    suspend fun deletePatient(
        @Path("id") id: Long
    ): Response<String>
}