package com.jgdeveloppement.jg_foot.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.FinalComment
import com.jgdeveloppement.jg_foot.models.Liked
import com.jgdeveloppement.jg_foot.models.User
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
        fun likedRef(docId: String) = firebaseFirestore.collection("comment").document(docId).collection("liked")
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
    fun addLiked(liked: Liked, docId: String): Task<Void> { return likedRef(docId).document(liked.id).set(liked) }
    
    //Delete
    fun deleteComment(commentId: String): Task<Void> { return commentRef.document(commentId).delete() }
    private fun deleteLike(commentId: String, likedId: String): Task<Void> { return likedRef(commentId).document(likedId).delete() }


    //Update
    private fun updateCount(commentId: String, likeCount: Int): Task<Void>{ return  commentRef.document(commentId).update("countLike", likeCount) }

    fun updateLikeCount(commentId: String, haveLike: Boolean, userId: String, callback: ()->Unit){
        commentRef.document(commentId).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful){
                val comment = task.result!!.toObject(Comment::class.java)
                val likeCount: Int
                if (!haveLike){
                    likeCount = comment!!.countLike +1
                    updateCount(commentId, likeCount)
                    callback()
                } else {
                    likeCount = comment!!.countLike -1
                    var wait = 0
                    likedRef(commentId).whereEqualTo("userId", userId).get().addOnCompleteListener { likedTask: Task<QuerySnapshot> ->
                        if (likedTask.isSuccessful){
                            val n = likedTask.result!!.size()
                            for (liked in likedTask.result!!.toObjects(Liked::class.java)){
                                deleteLike(commentId, liked.id)
                                wait++
                                Log.i("DEBUGGG", "wait = $wait n = $n")
                                if (wait == n){
                                    updateCount(commentId, likeCount)
                                    callback()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //Get
    fun getCommentReferenceId(): String {
        val reference: DocumentReference = commentRef.document()
        return reference.id
    }

    fun getLikeReferenceId(commentId: String): String {
        val reference: DocumentReference = likedRef(commentId).document()
        return reference.id
    }

    fun getUser(id: String) : MutableLiveData<User>{
        val user = MutableLiveData<User>()
        userRef.document(id).get().addOnCompleteListener { task: Task<DocumentSnapshot> ->
            if (task.isSuccessful) user.value = task.result!!.toObject(User::class.java)
        }

        return user
    }


    fun getAllComments(fromId: String, userId: String) : MutableLiveData<List<FinalComment>> {
        val commentList = mutableListOf<FinalComment>()
        val finalCommentList = MutableLiveData<List<FinalComment>>()
        commentRef.whereEqualTo("fromId", fromId).get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful){
                for (comment in task.result!!.toObjects(Comment::class.java)){
                    val finalComment = FinalComment(comment.id, comment.userId, comment.userName, comment.userUrlImage,
                        comment.fromId, comment.createdAt, comment.comment, comment.countLike, comment.countComment)

                    commentRef.whereEqualTo("fromId", comment.id).get().addOnCompleteListener { commentTask: Task<QuerySnapshot> ->
                        if (commentTask.isSuccessful) finalComment.countComment = commentTask.result!!.size()

                        likedRef(comment.id).get().addOnCompleteListener { likedTask: Task<QuerySnapshot> ->
                            if (likedTask.isSuccessful){
                                for (like in likedTask.result!!.toObjects(Liked::class.java)){
                                    if (like.userId == userId){
                                        finalComment.haveLiked = true
                                    }
                                }
                                commentList.add(finalComment)
                                val finalList = commentList.sortedByDescending { it.createdAt }
                                finalCommentList.value = finalList
                            }
                        }

                    }
                }
            }
        }

        return finalCommentList
    }
}