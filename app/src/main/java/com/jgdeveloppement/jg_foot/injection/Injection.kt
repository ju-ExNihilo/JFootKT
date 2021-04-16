package com.jgdeveloppement.jg_foot.injection

import com.jgdeveloppement.jg_foot.factory.ViewModelFactory
import com.jgdeveloppement.jg_foot.repository.MainRepository
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.RetrofitBuilder

object Injection {

    private fun provideMainRepository() : MainRepository{
        val apiHelper = ApiHelper(RetrofitBuilder.apiService)
        return MainRepository(apiHelper)
    }

    fun provideMainViewModelFactory() : ViewModelFactory {
        val mainRepository = provideMainRepository()
        return ViewModelFactory(mainRepository)
    }
}