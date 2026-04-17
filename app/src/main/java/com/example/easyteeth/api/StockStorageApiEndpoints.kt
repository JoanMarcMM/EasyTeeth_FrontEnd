package com.example.easyteeth.api

import com.example.easyteeth.model.StockReductionRequest
import com.example.easyteeth.model.StockStorage
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StockStorageApiEndpoints {
    @GET("stockStorage/storage/{storageId}")
    suspend fun getStockByStorage(@Path("storageId") storageId: Long): Response<List<StockStorage>>

    @POST("stockStorage/reduce")
    suspend fun reduceStockForBox(@Body request: StockReductionRequest): Response<Unit>

    @POST("stockStorage/restore")
    suspend fun restoreStockFromBox(@Body request: StockReductionRequest): Response<Unit>
}
