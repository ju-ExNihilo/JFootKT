package com.jgdeveloppement.jg_foot.viewmodel

import androidx.lifecycle.ViewModel
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.Liked
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
    fun getAllComments(fromId: String, userId: String) = mainRepository.getAllComments(fromId, userId)

    //Like
    fun getLikeReferenceId(commentId: String) = mainRepository.getLikeReferenceId(commentId)
    fun addLiked(liked: Liked, docId: String) = mainRepository.addLiked(liked, docId)
    fun updateLikeCount(commentId: String, haveLike: Boolean, userId: String, callback: ()->Unit) =
        mainRepository.updateLikeCount(commentId, haveLike, userId, callback)
}