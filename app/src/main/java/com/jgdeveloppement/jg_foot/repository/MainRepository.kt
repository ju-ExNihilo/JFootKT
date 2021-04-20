package com.jgdeveloppement.jg_foot.repository

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.*
import com.jgdeveloppement.jg_foot.models.*
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.commentRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.likedRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.userRef
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.Resource
import kotlinx.coroutines.Dispatchers

class MainRepository(private val apiHelper: ApiHelper) {

    object Singleton{
        private val firebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = firebaseFirestore.collection("user")
        val commentRef = firebaseFirestore.collection("comment")
        fun likedRef(docId: String) = commentRef.document(docId).collection("liked")
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
    fun addUser(user: User): Task<Void> { return userRef.document(user.id).set(user) }
    fun addComment(comment: Comment): Task<Void> { return commentRef.document(comment.id).set(comment) }
    private fun addLiked(commentId: String, liked: Liked): Task<Void> { return likedRef(commentId).document(liked.userId).set(liked) }
    
    //Delete
    fun deleteComment(commentId: String): Task<Void> { return commentRef.document(commentId).delete() }
    private fun deleteLiked(commentId: String, likedId: String): Task<Void> { return likedRef(commentId).document(likedId).delete() }

    //Update
    private fun incrementLike(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countLike", FieldValue.increment(1)) }
    private fun decrementLike(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countLike", FieldValue.increment(-1)) }
    fun incrementComment(commentId: String): Task<Void>{ return commentRef.document(commentId).update("countComment", FieldValue.increment(1)) }

    fun updateLikeCount(commentId: String, userId: String){
        likedRef(commentId).get().addOnCompleteListener { likedTask: Task<QuerySnapshot> ->
            if (likedTask.isSuccessful){
                val likedList = likedTask.result!!.toObjects(Liked::class.java)
                val haveLike = likedList.filter { it.userId.equals(userId, ignoreCase = true) }

                if (haveLike.isEmpty()) {
                    val liked = Liked(userId, false)
                    addLiked(commentId, liked)
                    incrementLike(commentId)
                } else{
                    deleteLiked(commentId, userId)
                    decrementLike(commentId)
                }
            }
        }
    }

    //Get
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
}