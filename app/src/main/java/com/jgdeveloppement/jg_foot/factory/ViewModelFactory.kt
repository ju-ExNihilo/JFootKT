package com.jgdeveloppement.jg_foot.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jgdeveloppement.jg_foot.repository.MainRepository
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel

class ViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)){
            return MainViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}