package com.jgdeveloppement.jg_foot.webview

import android.view.View
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import androidx.core.widget.NestedScrollView

class MyWebChromeClient(private var fullscreenContainer: FrameLayout, private val mainContainer: NestedScrollView) : WebChromeClient()  {

    private lateinit var fullscreen: View

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)

        if (view is FrameLayout){
            fullscreen = view
            fullscreenContainer.addView(fullscreen)
            fullscreenContainer.visibility = View.VISIBLE
            mainContainer.visibility = View.GONE

        }
    }

    override fun onHideCustomView() {
        super.onHideCustomView()

        fullscreenContainer.removeView(fullscreen)
        fullscreenContainer.visibility = View.GONE
        mainContainer.visibility = View.VISIBLE
    }
}