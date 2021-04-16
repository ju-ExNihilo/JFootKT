package com.jgdeveloppement.jg_foot.viewmodel

import androidx.lifecycle.ViewModel
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getAllMatches() = mainRepository.getAllMatches()

    //FireStore
    fun getReferenceId() = mainRepository.getReferenceId()
    fun addUser(user: User) = mainRepository.addUser(user)
    fun getUser(id: String) = mainRepository.getUser(id)
}