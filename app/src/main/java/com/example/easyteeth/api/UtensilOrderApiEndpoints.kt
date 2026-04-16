package com.example.easyteeth.api

import com.example.easyteeth.model.UtensilOrderRequest
import com.example.easyteeth.model.UtensilOrderResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UtensilOrderApiEndpoints {
    @POST("utensilOrder/new")
    suspend fun createOrder(@Body request: UtensilOrderRequest): Response<Any>

    @GET("utensilOrder/index")
    suspend fun getAllOrders(): Response<List<UtensilOrderResponse>>

    @PUT("utensilOrder/{id}/markArrived")
    suspend fun markOrderArrived(@Path("id") orderId: Long): Response<UtensilOrderResponse>
}
