package com.jgdeveloppement.jg_foot.reply

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityReplyBinding
import com.jgdeveloppement.jg_foot.dialog.BadWordPopup
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel
import java.io.Serializable

class ReplyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReplyBinding
    private lateinit var mainViewModel: MainViewModel
    private var finalComment: Comment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        setupViewModel()

        val intent = intent
        if (intent != null){
            if (intent.hasExtra(Utils.RC_COMMENT)){
                finalComment = intent.extras?.get(Utils.RC_COMMENT) as Comment
                mainViewModel.getUser(getUserId()).observe(this, {
                    initCommentInView(finalComment!!, mainViewModel, this, binding.replyUserNameTextView, binding.replyCartBadgeLike,
                        binding.replyDateTextView, binding.replyCommentTextView, binding.replyAvatarImageView, binding.replyLikeLayout,
                        getUserId(), it.name)
                })
                addComment()
            }
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbarReply)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addComment(){
        binding.replyAddButton.setOnClickListener {
            val badWord = resources.getStringArray(R.array.bad_word)
            val message = binding.replyEditText.text.toString()
            val id = mainViewModel.getCommentReferenceId()
            val userId = getUserId()
            val haveBadWord = badWord.filter { message.contains(it, ignoreCase = true) }

            if (haveBadWord.isEmpty()){
                mainViewModel.getUser(userId).observe(this, { user ->
                    val userName = user.name
                    val userUrlImage = user.avatarUrl

                    if (message.isNotBlank()) {
                        val comment = Comment(id, userId, userName, userUrlImage, finalComment!!.id, message)
                        mainViewModel.addComment(comment, finalComment)
                        Utils.haveReply = true
                        onBackPressed()
                    }
                })
            }else{
                BadWordPopup(this).show()
            }
        }
    }

    fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, finalComment: Comment, viewClicked: View) {
            val intent = Intent(activity, ReplyActivity::class.java)
            intent.putExtra(Utils.RC_COMMENT, finalComment as Serializable)
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, viewClicked, activity!!.getString(R.string.transition_animation_comment))
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }

        fun initCommentInView(comment: Comment, mainViewModel: MainViewModel, context: Context, userName: TextView, countLike: TextView, date: TextView,
                              message: TextView, avatar: ImageView, likeLayout: FrameLayout, userId: String, displayName: String){

            if (comment.countLike == 0){
                countLike.visibility = View.GONE
            }else{
                countLike.text = comment.countLike.toString()
                countLike.visibility = View.VISIBLE
            }
            userName.text = comment.userName
            if (comment.userUrlImage != "none") Glide.with(context).load(Uri.parse(comment.userUrlImage)).circleCrop().into(avatar)
            date.text =  Utils.getFormatDateTime(comment.createdAt)
            message.text = comment.comment

//            likeLayout.setOnClickListener {
//                mainViewModel.updateLikeCount(comment.id, userId, displayName, comment.userId)
//                mainViewModel.getComment(comment.id).
//            }
        }
    }
}
