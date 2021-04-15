package com.jgdeveloppement.jg_foot.viewmodel

import androidx.lifecycle.ViewModel
import com.jgdeveloppement.jg_foot.repository.MatchRepository

class MatchViewModel(private val matchRepository: MatchRepository) : ViewModel() {

    fun getAllMatches() = matchRepository.getAllMatches()
}