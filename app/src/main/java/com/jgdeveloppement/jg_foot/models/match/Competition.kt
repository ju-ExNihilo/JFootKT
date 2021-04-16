package com.jgdeveloppement.jg_foot.models.match

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Competition(
    @SerializedName("name")
    @Expose
    val name: String,
    @SerializedName("id")
    @Expose
    val id: Int,
    @SerializedName("url")
    @Expose
    val url: String
)

