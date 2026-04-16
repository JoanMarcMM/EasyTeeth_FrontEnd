package com.example.easyteeth.api

import com.example.easyteeth.model.Storage
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface StorageApiEndpoints {
    @GET("storage/index")
    suspend fun getAllStorages(): Response<List<Storage>>

    @GET("storage/{id}")
    suspend fun getStorageById(@Path("id") id: Long): Response<Storage>
}
