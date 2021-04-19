package com.jgdeveloppement.jg_foot.reply

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityDetailsBinding
import com.jgdeveloppement.jg_foot.databinding.ActivityReplyBinding
import com.jgdeveloppement.jg_foot.details.DetailsActivity
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.FinalComment
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel
import com.jgdeveloppement.jg_foot.webview.MyWebChromeClient
import com.jgdeveloppement.jg_foot.webview.MyWebViewClient
import java.io.Serializable

class ReplyActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReplyBinding
    private lateinit var mainViewModel: MainViewModel
    private var finalComment: FinalComment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReplyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        setupViewModel()

        val intent = intent
        if (intent != null){
            if (intent.hasExtra(Utils.RC_COMMENT) ){
                finalComment = intent.extras?.get(Utils.RC_COMMENT) as FinalComment
                initView()
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


        if (finalComment?.haveLiked == true){
            binding.replyLikeImage.setImageDrawable(resources.getDrawable(R.drawable.ic_like, null))
        }else{
            binding.replyLikeImage.setImageDrawable(resources.getDrawable(R.drawable.ic_unlike, null))
        }

        //Comment
        binding.replyUserNameTextView.text = finalComment?.userName
        if (finalComment?.userUrlImage != "none") Glide.with(this).load(Uri.parse(finalComment?.userUrlImage)).circleCrop().into(binding.replyAvatarImageView)
        binding.replyDateTextView.text = finalComment?.createdAt?.let { Utils.getFormatDateTime(it) }
        binding.replyCommentTextView.text = finalComment?.comment
    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, finalComment: FinalComment, viewClicked: View) {
            val intent = Intent(activity, ReplyActivity::class.java)
            intent.putExtra(Utils.RC_COMMENT, finalComment as Serializable)
            val options = ActivityOptions.makeSceneTransitionAnimation(activity, viewClicked, activity!!.getString(R.string.transition_animation_comment))
            ActivityCompat.startActivity(activity, intent, options.toBundle())
        }
    }
}
