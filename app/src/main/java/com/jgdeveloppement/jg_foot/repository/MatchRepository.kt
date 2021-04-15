package com.jgdeveloppement.jg_foot.repository

import androidx.lifecycle.liveData
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.Resource
import kotlinx.coroutines.Dispatchers

class MatchRepository(private val apiHelper: ApiHelper) {

    fun getAllMatches() = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.getAllMatches()))
        }catch (exception: Exception){
            emit(Resource.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

}