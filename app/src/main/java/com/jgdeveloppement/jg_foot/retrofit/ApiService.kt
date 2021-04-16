package com.jgdeveloppement.jg_foot.retrofit

import com.jgdeveloppement.jg_foot.models.match.Match
import retrofit2.http.GET

interface ApiService {

    @GET(".")
    suspend fun getAllMatches() : List<Match>
}