package com.example.easyteeth.model

import com.google.gson.annotations.SerializedName

data class Storage(
    val id: Long? = null,
    @SerializedName("numStorage")
    val numStorage: Int = 0
)
