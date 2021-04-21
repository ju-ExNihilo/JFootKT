package com.jgdeveloppement.jg_foot.viewmodel

import androidx.lifecycle.ViewModel
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.Notification
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.repository.MainRepository

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    //Retrofit
    fun getAllMatches() = mainRepository.getAllMatches()

    //FireStore

    //User
    fun addUser(user: User) = mainRepository.addUser(user)
    fun getUser(id: String) = mainRepository.getUser(id)

    //Comment
    fun getCommentReferenceId() = mainRepository.getCommentReferenceId()
    fun addComment(comment: Comment, fromComment: Comment?) = mainRepository.addComment(comment, fromComment)
    fun deleteComment(comment: Comment) = mainRepository.deleteComment(comment)
    fun getLiveAllComment(fromId: String) = mainRepository.getLiveAllComment(fromId)

    //Like
    fun updateLikeCount(commentId: String, userId: String, userName: String, forId: String) = mainRepository.updateLikeCount(commentId, userId, userName, forId)

    //Notification
    fun getLiveNotifications(userId: String) = mainRepository.getLiveNotifications(userId)
    fun getLiveNotificationCount(userId: String, callback: ()->Unit) = mainRepository.getLiveNotificationCount(userId, callback)
    fun getNotificationCount(userId: String) = mainRepository.getNotificationCount(userId)
    fun deleteNotification(userId: String, notificationId: String) = mainRepository.deleteNotification(userId, notificationId)
    fun updateNotification(userId: String) = mainRepository.updateNotification(userId)
    fun getComment(commentId: String) = mainRepository.getComment(commentId)
}