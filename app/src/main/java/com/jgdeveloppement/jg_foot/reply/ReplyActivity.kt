package com.jgdeveloppement.jg_foot.reply

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
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
                initView()
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

    private fun initView(){
        //Like And Comment
        if (finalComment?.countLike == 0){
            binding.replyCartBadgeLike.visibility = View.GONE
        }else{
            binding.replyCartBadgeLike.text = finalComment?.countLike.toString()
            binding.replyCartBadgeLike.visibility = View.VISIBLE
        }

        //Comment
        binding.replyUserNameTextView.text = finalComment?.userName
        if (finalComment?.userUrlImage != "none") Glide.with(this).load(Uri.parse(finalComment?.userUrlImage)).circleCrop().into(binding.replyAvatarImageView)
        binding.replyDateTextView.text = finalComment?.createdAt?.let { Utils.getFormatDateTime(it) }
        binding.replyCommentTextView.text = finalComment?.comment
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
                        mainViewModel.addComment(comment)
                        Utils.haveReply = true
                        mainViewModel.updateCommentCount(finalComment!!.id)
                        onBackPressed()
                    }
                })
            }else{
                BadWordPopup(this).show()
            }

        }
    }

    private fun getUserId() = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, finalComment: Comment, viewClicked: View) {
            val intent = Intent(activity, ReplyActivity::class.java)
            intent.putExtra(Utils.RC_COMMENT, finalComment as Serializable)
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, viewClicked, activity!!.getString(R.string.transition_animation_comment))
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
