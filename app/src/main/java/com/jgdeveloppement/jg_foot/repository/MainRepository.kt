package com.jgdeveloppement.jg_foot.repository

import android.content.res.Resources
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firepush.Fire
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.*
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.commentRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.likedRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.notificationRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.userRef
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.Resource
import kotlinx.coroutines.Dispatchers
import java.util.*

class MainRepository(private val apiHelper: ApiHelper) {

    object Singleton{
        private val firebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = firebaseFirestore.collection("user")
        val commentRef = firebaseFirestore.collection("comment")
        fun likedRef(commentId: String) = commentRef.document(commentId).collection("liked")
        fun notificationRef(userId: String) = userRef.document(userId).collection("notification")
    }

    //Retrofit
    fun getAllMatches() = liveData(Dispatchers.IO){
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = apiHelper.getAllMatches()))
        }catch (exception: Exception){
            emit(Resource.error(data = null, message = exception.message?: "Error Occurred!"))
        }
    }

    //FireStore

    //Insert
    private fun addCommentP(comment: Comment): Task<Void> { return commentRef.document(comment.id).set(comment) }
    private fun addNotification(userId: String, notification: Notification): Task<Void> { return notificationRef(userId).document(notification.id).set(notification) }
    private fun addLiked(commentId: String, liked: Liked): Task<Void> { return likedRef(commentId).document(liked.userId).set(liked) }

    fun addUser(user: User): Task<Void> { return userRef.document(user.id).set(user) }

    fun addComment(comment: Comment, fromComment: Comment?){
        addCommentP(comment)
        if (fromComment != null) {
            incrementComment(fromComment.id)
            if (comment.userId != fromComment.userId){
                val notificationId = getNotificationReferenceId(fromComment.userId)
                val notification = Notification(notificationId, comment.userName, comment.id, false, fromComment.id, false, Date())
                addNotification(fromComment.userId, notification)
                sendNotification(fromComment.userId, comment.userName, Resources.getSystem().getString(R.string.reply_notification))
            }
        }
    }

    //Delete
    private fun deleteLiked(commentId: String, likedId: String): Task<Void> { return likedRef(commentId).document(likedId).delete() }
    fun deleteNotification(userId: String, notificationId: String): Task<Void> { return notificationRef(userId).document(notificationId).delete() }

    fun deleteComment(comment: Comment): Task<Void> {
        isFromComment(comment.fromId)
        return commentRef.document(comment.id).delete()
    }

    //Update
    private fun incrementLike(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countLike", FieldValue.increment(1)) }
    private fun decrementLike(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countLike", FieldValue.increment(-1)) }
    private fun incrementComment(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countComment", FieldValue.increment(1)) }
    private fun decrementComment(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countComment", FieldValue.increment(-1)) }
    private fun updateNotificationP(userId: String, docId: String): Task<Void>{ return notificationRef(userId).document(docId).update("checked", true) }

    fun updateLikeCount(commentId: String, userId: String, userName: String, forId: String){
        likedRef(commentId).get().addOnCompleteListener { likedTask: Task<QuerySnapshot> ->
            if (likedTask.isSuccessful){
                val likedList = likedTask.result!!.toObjects(Liked::class.java)
                val haveLike = likedList.filter { it.userId.equals(userId, ignoreCase = true) }

                if (haveLike.isEmpty()) {
                    val liked = Liked(userId)
                    addLiked(commentId, liked)
                    incrementLike(commentId)
                    if (userId != forId) {
                        val notification = Notification(commentId, userName, commentId, true, "none", false, Date())
                        addNotification(forId, notification)
                    }
                } else{
                    deleteLiked(commentId, userId)
                    decrementLike(commentId)
                    sendNotification(forId, userName, Resources.getSystem().getString(R.string.like_notification))
                }
            }
        }
    }

    fun updateLikeCount(commentId: String, userId: String, userName: String, forId: String, callback: ()->Unit){
        likedRef(commentId).get().addOnCompleteListener { likedTask: Task<QuerySnapshot> ->
            if (likedTask.isSuccessful){
                val likedList = likedTask.result!!.toObjects(Liked::class.java)
                val haveLike = likedList.filter { it.userId.equals(userId, ignoreCase = true) }

                if (haveLike.isEmpty()) {
                    val liked = Liked(userId)
                    addLiked(commentId, liked)
                    incrementLike(commentId)
                    if (userId != forId) {
                        val notification = Notification(commentId, userName, commentId, true, "none", false, Date())
                        addNotification(forId, notification)
                        sendNotification(forId, userName, Resources.getSystem().getString(R.string.like_notification))
                    }
                } else{
                    deleteLiked(commentId, userId)
                    decrementLike(commentId)
                }
                callback()
            }
        }
    }

    private fun sendNotification(userId: String, userName: String, textNotification: String) {
        userRef.document(userId).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val user = task.result!!.toObject(User::class.java)
                user?.token?.let {
                    if (it != "none") Fire.create().setTitle("JFoot-Notification").setBody("$userName $textNotification").toIds(it).push()
                }
            }
        }

    }

    //Get
    private fun getNotificationReferenceId(userId: String): String {
        val reference: DocumentReference = notificationRef(userId).document()
        return reference.id
    }

    private fun isFromComment(id: String) {
        commentRef.document(id).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) {
                val comment = task.result!!.toObject(Comment::class.java)
                if (comment != null) decrementComment(comment.id)
            }
        }
    }

    fun getComment(commentId: String): MutableLiveData<Comment>{
        val comment = MutableLiveData<Comment>()
        commentRef.document(commentId).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) comment.value = task.result!!.toObject(Comment::class.java)
        }
        return comment
    }

    fun getCommentReferenceId(): String {
        val reference: DocumentReference = commentRef.document()
        return reference.id
    }


    fun getUser(id: String) : MutableLiveData<User>{
        val user = MutableLiveData<User>()
        userRef.document(id).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) user.value = task.result!!.toObject(User::class.java)
        }
        return user
    }

    fun getLiveAllComment(fromId: String): FirestoreRecyclerOptions<Comment> {
        val query = commentRef.whereEqualTo("fromId", fromId).orderBy("createdAt", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment::class.java).build()
    }

    fun getLiveNotifications(userId: String): FirestoreRecyclerOptions<Notification> {
        val query = notificationRef(userId).orderBy("createdAt", Query.Direction.DESCENDING)
        return FirestoreRecyclerOptions.Builder<Notification>().setQuery(query, Notification::class.java).build()
    }

    fun getNotificationCount(userId: String): MutableLiveData<List<Notification>>{
        val notificationList = MutableLiveData<List<Notification>>()
        notificationRef(userId).whereEqualTo("checked", false).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) notificationList.value = task.result!!.toObjects(Notification::class.java)
        }
        return notificationList
    }

    fun updateNotification(userId: String){
        notificationRef(userId).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful) {
                for (doc in task.result!!.toObjects(Notification::class.java)){
                    updateNotificationP(userId, doc.id)
                }
            }
        }
    }

    fun getLiveNotificationCount(userId: String, callback: ()->Unit){
        notificationRef(userId).whereEqualTo("checked", false).addSnapshotListener { snapshots, error ->
                    if (error != null){
                        return@addSnapshotListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.REMOVED) callback()
                    }
                }
    }

    fun registerToken(userId: String){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }
            val token = task.result
            FirebaseFirestore.getInstance().collection("user").document(userId).update("token", token)
        })
    }
}