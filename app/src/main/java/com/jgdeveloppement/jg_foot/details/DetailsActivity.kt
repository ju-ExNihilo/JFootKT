package com.jgdeveloppement.jg_foot.details

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.auth.FirebaseAuth
import com.jgdeveloppement.jg_foot.databinding.ActivityDetailsBinding
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.Liked
import com.jgdeveloppement.jg_foot.utils.Utils.RC_MATCH_ID
import com.jgdeveloppement.jg_foot.webview.MyWebViewClient
import com.jgdeveloppement.jg_foot.utils.Utils.RC_MATCH_TITLE
import com.jgdeveloppement.jg_foot.utils.Utils.RC_MATCH_URL
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel
import com.jgdeveloppement.jg_foot.webview.MyWebChromeClient
import com.konifar.fab_transformation.FabTransformation

class DetailsActivity : AppCompatActivity(), CommentAdapter.OnCommentClicked {

    private lateinit var binding: ActivityDetailsBinding
    private lateinit var mainViewModel: MainViewModel
    private var matchId: String? = null

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()
        setupViewModel()

        val intent = intent
        if (intent != null){
            if (intent.hasExtra(RC_MATCH_URL) && intent.hasExtra(RC_MATCH_TITLE) && intent.hasExtra(RC_MATCH_ID)){
                val matchUrl = intent.getStringExtra(RC_MATCH_URL)
                val matchTitle = intent.getStringExtra(RC_MATCH_TITLE)
                matchId = intent.getStringExtra(RC_MATCH_ID)

                binding.detailsMatchTitle.text = matchTitle

                binding.detailsWebView.webViewClient =  MyWebViewClient(this)
                binding.detailsWebView.loadUrl(matchUrl)
                binding.detailsWebView.settings.allowContentAccess = true
                binding.detailsWebView.settings.javaScriptEnabled = true
                binding.detailsWebView.webChromeClient = MyWebChromeClient(binding.fullscreenContainer, binding.detailsContainer)

                initCommentList()
                initAddCommentLayout()
                addComment()

            }
        }
    }

    override fun onBackPressed() {
        if (binding.addCommentLayout.visibility == View.VISIBLE){
            closeAddCommentLayout()
        }else{
            super.onBackPressed()
        }
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private fun initToolbar(){
        setSupportActionBar(binding.toolbarDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    private fun initCommentList(){
        mainViewModel.getAllComments(matchId!!, getUserId()).observe(this, {
            binding.commentRecyclerView.adapter = CommentAdapter(this, it, this)
            for (comment in it){
                Log.i("DEBUGGG", comment.toString())
            }
        })
    }

    private fun initAddCommentLayout(){
        binding.addFloatingButton.setOnClickListener {
            binding.commentRecyclerView.visibility = View.GONE
            FabTransformation.with(binding.addFloatingButton).transformTo(binding.addCommentLayout as View)
        }

        binding.closeCommentButton.setOnClickListener {
            closeAddCommentLayout()
        }
    }

    private fun closeAddCommentLayout(){
        FabTransformation.with(binding.addFloatingButton).transformFrom(binding.addCommentLayout as View)
        binding.commentRecyclerView.visibility = View.VISIBLE
    }

    private fun addComment(){
        binding.addCommentAddButton.setOnClickListener {
            val message = binding.addCommentEditText.text.toString()
            val id = mainViewModel.getCommentReferenceId()
            val userId = FirebaseAuth.getInstance().currentUser!!.uid

            mainViewModel.getUser(userId).observe(this, { user ->
                val userName = user.name
                val userUrlImage = user.avatarUrl

                if (message.isNotBlank()){
                    val comment = Comment(id, userId, userName, userUrlImage, matchId!!, message)
                    mainViewModel.addComment(comment)
                    closeAddCommentLayout()
                    initCommentList()
                }
            })
        }
    }

    override fun onClickedLike(commentId: String, haveLiked: Boolean) {
        val liked = Liked(mainViewModel.getLikeReferenceId(commentId), getUserId())
        mainViewModel.addLiked(liked, commentId)
        mainViewModel.updateLikeCount(commentId, haveLiked, getUserId()) { initCommentList() }

    }

    override fun onClickedComment(commentId: String) {

    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, matchUrl: String, matchTitle: String, matchId: String) {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(RC_MATCH_URL, matchUrl)
            intent.putExtra(RC_MATCH_TITLE, matchTitle)
            intent.putExtra(RC_MATCH_ID, matchId)
            ActivityCompat.startActivity(activity!!, intent, null)
        }

    }
}
