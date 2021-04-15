package com.jgdeveloppement.jg_foot.retrofit

class ApiHelper(private val apiService: ApiService) {

    suspend fun getAllMatches() = apiService.getAllMatches()
}