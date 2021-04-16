package com.jgdeveloppement.jg_foot.models.match

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Match(
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("embed")
    @Expose
    val embed: String,
    @SerializedName("url")
    @Expose
    val url: String,
    @SerializedName("thumbnail")
    @Expose
    val thumbnail: String,
    @SerializedName("date")
    @Expose
    val date: String,
    @SerializedName("side1")
    @Expose
    val side1: Side1,
    @SerializedName("side2")
    @Expose
    val side2: Side2,
    @SerializedName("competition")
    @Expose
    val competition: Competition,
    @SerializedName("videos")
    @Expose
    val videos: List<Video>)
