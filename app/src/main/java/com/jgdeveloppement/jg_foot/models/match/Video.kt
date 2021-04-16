package com.jgdeveloppement.jg_foot.models.match

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("embed")
    @Expose
    val embed: String
)
