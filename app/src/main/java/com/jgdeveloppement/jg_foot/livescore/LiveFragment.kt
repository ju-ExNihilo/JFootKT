package com.jgdeveloppement.jg_foot.livescore

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat.startActivity
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentLiveBinding
import com.jgdeveloppement.jg_foot.utils.Utils.URL_LIVE_SCORE


class LiveFragment : Fragment() {

    private var _binding: FragmentLiveBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentLiveBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.liveScoreView.webViewClient = context?.let { MyWebViewClient(it) }
        binding.liveScoreView.loadUrl(URL_LIVE_SCORE)
        binding.liveScoreView.settings.allowContentAccess = true
        binding.liveScoreView.settings.javaScriptEnabled = true

    }


}
