package com.jgdeveloppement.jg_foot.viewmodel

import androidx.lifecycle.ViewModel
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getAllMatches() = mainRepository.getAllMatches()

    //FireStore

    //User
    fun addUser(user: User) = mainRepository.addUser(user)
    fun getUser(id: String) = mainRepository.getUser(id)

    //Comment
    fun getCommentReferenceId() = mainRepository.getCommentReferenceId()
    fun addComment(comment: Comment) = mainRepository.addComment(comment)
    fun deleteComment(comment: Comment) = mainRepository.deleteComment(comment)
    fun updateCommentCount(commentId: String) = mainRepository.incrementComment(commentId)
    fun getLiveAllComment(fromId: String) = mainRepository.getLiveAllComment(fromId)

    //Like
    fun updateLikeCount(commentId: String, userId: String) = mainRepository.updateLikeCount(commentId, userId)
}