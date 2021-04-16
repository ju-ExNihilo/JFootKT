package com.jgdeveloppement.jg_foot.details

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityDetailsBinding
import com.jgdeveloppement.jg_foot.home.HomeActivity
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.webview.MyWebViewClient
import com.jgdeveloppement.jg_foot.utils.Utils.RC_MATCH_TITLE
import com.jgdeveloppement.jg_foot.utils.Utils.RC_MATCH_URL
import com.jgdeveloppement.jg_foot.webview.MyWebChromeClient
import com.konifar.fab_transformation.FabTransformation

class DetailsActivity : AppCompatActivity(), CommentAdapter.OnCommentClicked {

    private lateinit var binding: ActivityDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initToolbar()

        val intent = intent
        if (intent != null){
            if (intent.hasExtra(RC_MATCH_URL)){
                val matchUrl = intent.getStringExtra(RC_MATCH_URL)
                val matchTitle = intent.getStringExtra(RC_MATCH_TITLE)

                binding.detailsMatchTitle.text = matchTitle

                binding.detailsWebView.webViewClient =  MyWebViewClient(this)
                binding.detailsWebView.loadUrl(matchUrl)
                binding.detailsWebView.settings.allowContentAccess = true
                binding.detailsWebView.settings.javaScriptEnabled = true
                binding.detailsWebView.webChromeClient = MyWebChromeClient(binding.fullscreenContainer, binding.detailsContainer)

                initCommentList()

                binding.addFloatingButton.setOnClickListener {
                    val isVisible = binding.toolbarFooter.visibility
                    Log.i("DEBUGGG", isVisible.toString())
                    binding.commentRecyclerView.visibility = View.GONE
                    FabTransformation.with(binding.addFloatingButton).transformTo(binding.toolbarFooter as View)
                }

                binding.closeCommentButton?.setOnClickListener {
                    val isVisible = binding.toolbarFooter.visibility
                    Log.i("DEBUGGG", isVisible.toString())
                    FabTransformation.with(binding.addFloatingButton).transformFrom(binding.toolbarFooter as View)
                    binding.commentRecyclerView.visibility = View.VISIBLE
                }
            }
        }
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

    private fun initCommentList(){
        val commentList = listOf(Comment(), Comment(), Comment(), Comment())
        binding.commentRecyclerView.adapter = CommentAdapter(this, commentList, this)
    }

    override fun onClickedLike(commentId: String) {

    }

    override fun onClickedComment(commentId: String) {

    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?, matchUrl: String, matchTitle: String) {
            val intent = Intent(activity, DetailsActivity::class.java)
            intent.putExtra(RC_MATCH_URL, matchUrl)
            intent.putExtra(RC_MATCH_TITLE, matchTitle)
            ActivityCompat.startActivity(activity!!, intent, null)
        }

    }
}
