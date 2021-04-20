package com.jgdeveloppement.jg_foot.reply

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityReplyBinding
import com.jgdeveloppement.jg_foot.databinding.ActivityReplyListBinding
import com.jgdeveloppement.jg_foot.details.CommentAdapter
import com.jgdeveloppement.jg_foot.dialog.BadWordPopup
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.hideKeyboard
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel
import com.konifar.fab_transformation.FabTransformation
import java.io.Serializable

class ReplyListActivity : AppCompatActivity(), CommentAdapter.OnCommentClicked {

    private lateinit var binding: ActivityReplyListBinding
    private lateinit var mainViewModel: MainViewModel
    private var finalComment: Comment? = null
    private var adapter: CommentAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReplyListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        setupViewModel()

        val intent = intent
        if (intent != null){
            if (intent.hasExtra(Utils.RC_COMMENT)){
                finalComment = intent.extras?.get(Utils.RC_COMMENT) as Comment
                initView()
                initAddCommentLayout()
                addComment()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (Utils.haveReply){
            Utils.haveReply = false
            Utils.showSnackBar(binding.replyListFragmentLayout, getString(R.string.reply_been_send))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (adapter != null) {
            adapter!!.stopListening()
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbarReplyList)
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
            binding.replyListCartBadgeLike.visibility = View.GONE
        }else{
            binding.replyListCartBadgeLike.text = finalComment?.countLike.toString()
            binding.replyListCartBadgeLike.visibility = View.VISIBLE
        }

        //Comment
        binding.replyListUserNameTextView.text = finalComment?.userName
        if (finalComment?.userUrlImage != "none") Glide.with(this).load(Uri.parse(finalComment?.userUrlImage)).circleCrop().into(binding.replyListAvatarImageView)
        binding.replyListDateTextView.text = finalComment?.createdAt?.let { Utils.getFormatDateTime(it) }
        binding.replyListCommentTextView.text = finalComment?.comment
        initCommentList()
    }

    private fun initCommentList(){
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.replyListRecyclerView.animation = fadeIn
        adapter = CommentAdapter(this, mainViewModel.getLiveAllComment(finalComment!!.id), getUserId(), this)
        binding.replyListRecyclerView.adapter = adapter
        adapter!!.startListening()
    }

    private fun initAddCommentLayout(){
        binding.replyListAddFloatingButton.setOnClickListener {
            binding.replyListRecyclerView.visibility = View.GONE
            FabTransformation.with(binding.replyListAddFloatingButton).transformTo(binding.replyListAddCommentLayout as View)
        }
        binding.replyListCloseCommentButton.setOnClickListener {
            closeAddCommentLayout()
        }
    }

    private fun closeAddCommentLayout(){
        FabTransformation.with(binding.replyListAddFloatingButton).transformFrom(binding.replyListAddCommentLayout as View)
        binding.replyListRecyclerView.visibility = View.VISIBLE
    }

    private fun addComment(){
        binding.replyListAddCommentAddButton.setOnClickListener {
            val badWord = resources.getStringArray(R.array.bad_word)
            val message = binding.replyListAddCommentEditText.text.toString()
            val id = mainViewModel.getCommentReferenceId()
            val haveBadWord = badWord.filter { message.contains(it, ignoreCase = true) }

            if (haveBadWord.isEmpty()){
                mainViewModel.getUser(getUserId()).observe(this, { user ->
                    val userName = user.name
                    val userUrlImage = user.avatarUrl

                    if (message.isNotBlank()) {
                        val comment = Comment(id, getUserId(), userName, userUrlImage, finalComment!!.id, message)
                        mainViewModel.addComment(comment)
                        mainViewModel.updateCommentCount(finalComment!!.id)
                        applicationContext.hideKeyboard(binding.replyListFragmentLayout)
                        closeAddCommentLayout()
                        binding.replyListAddCommentEditText.text?.clear()
                    }
                })
            }else{
                BadWordPopup(this).show()
            }
        }
    }

    override fun onClickedLike(commentId: String) { mainViewModel.updateLikeCount(commentId, getUserId()) }

    override fun onClickedComment(comment: Comment, imageTransition: View) { ReplyActivity.navigate(this, comment, imageTransition) }

    override fun onClickedDeleteButton(comment: Comment) { mainViewModel.deleteComment(comment) }

    override fun onClickedItem(comment: Comment, imageTransition: View) {
        navigate(this, comment, imageTransition)
    }

    private fun getUserId() = FirebaseAuth.getInstance().currentUser!!.uid

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, finalComment: Comment, viewClicked: View) {
            val intent = Intent(activity, ReplyListActivity::class.java)
            intent.putExtra(Utils.RC_COMMENT, finalComment as Serializable)
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, viewClicked, activity!!.getString(R.string.transition_animation_comment))
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
