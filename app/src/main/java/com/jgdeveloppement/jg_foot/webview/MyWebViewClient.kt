package com.jgdeveloppement.jg_foot.webview

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity

class MyWebViewClient(private val context: Context) : WebViewClient() {

    override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {

        Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            startActivity(context, this, null)
        }
        return true
    }


}