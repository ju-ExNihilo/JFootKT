package com.jgdeveloppement.jg_foot.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Side2(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("url")
    @Expose
    val url: String
)
