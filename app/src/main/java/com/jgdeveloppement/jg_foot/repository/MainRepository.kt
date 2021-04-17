package com.jgdeveloppement.jg_foot.repository

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
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.commentRef
import com.jgdeveloppement.jg_foot.repository.MainRepository.Singleton.userRef
import com.jgdeveloppement.jg_foot.retrofit.ApiHelper
import com.jgdeveloppement.jg_foot.retrofit.Resource
import kotlinx.coroutines.Dispatchers

class MainRepository(private val apiHelper: ApiHelper) {

    object Singleton{
        private val firebaseFirestore = FirebaseFirestore.getInstance()
        val userRef = firebaseFirestore.collection("user")
        val commentRef = firebaseFirestore.collection("comment")
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
    fun addUser(user: User) = userRef.document(user.id).set(user)
    fun addComment(comment: Comment) = commentRef.document(comment.id).set(comment)

    //Get
    fun getReferenceId(): String {
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

    fun getAllComments() : MutableLiveData<List<Comment>> {
        val commentList = MutableLiveData<List<Comment>>()
        commentRef.get().addOnCompleteListener { task: Task<QuerySnapshot> ->
            if (task.isSuccessful){
                commentList.value = task.result!!.toObjects(Comment::class.java)
            }
        }

        return commentList
    }
}