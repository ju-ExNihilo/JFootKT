package com.jgdeveloppement.jg_foot.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jgdeveloppement.jg_foot.repository.MatchRepository
import com.jgdeveloppement.jg_foot.viewmodel.MatchViewModel

class ViewModelFactory(private val matchRepository: MatchRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MatchViewModel::class.java)){
            return MatchViewModel(matchRepository) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}