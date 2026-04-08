package com.example.easyteeth.api

import com.example.easyteeth.model.Pathology
import retrofit2.Response
import retrofit2.http.GET

interface PathologyApiEndpoints {
    
    @GET("pathology/index")
    suspend fun getAllPathologies(): Response<List<Pathology>>
}
