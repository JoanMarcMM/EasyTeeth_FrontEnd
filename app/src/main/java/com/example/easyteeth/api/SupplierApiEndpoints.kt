package com.example.easyteeth.api

import com.example.easyteeth.model.Supplier
import com.example.easyteeth.model.SupplierRequest
import retrofit2.Response
import retrofit2.http.*

interface SupplierApiEndpoints {
    @GET("supplier/index")
    suspend fun getAll(): Response<List<Supplier>>

    @GET("supplier/{id}")
    suspend fun getSupplierById(@Path("id") id: Long): Response<Supplier>

    @POST("supplier/new")
    suspend fun createSupplier(@Body request: SupplierRequest): Response<Supplier>

    @PUT("supplier/{id}")
    suspend fun updateSupplier(@Path("id") id: Long, @Body request: SupplierRequest): Response<Supplier>

    @DELETE("supplier/{id}")
    suspend fun deleteSupplier(@Path("id") id: Long): Response<String>
}
