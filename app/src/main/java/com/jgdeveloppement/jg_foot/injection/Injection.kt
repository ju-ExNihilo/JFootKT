package com.jgdeveloppement.jg_foot.injection

import com.jgdeveloppement.jg_foot.factory.ViewModelFactory
import com.jgdeveloppement.jg_foot.repository.MatchRepository
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.RetrofitBuilder

object Injection {

    private fun provideMatchRepository() : MatchRepository{
        val apiHelper = ApiHelper(RetrofitBuilder.apiService)
        return MatchRepository(apiHelper)
    }

    fun provideMatchViewModelFactory() : ViewModelFactory {
        val matchRepository = provideMatchRepository()
        return ViewModelFactory(matchRepository)
    }
}