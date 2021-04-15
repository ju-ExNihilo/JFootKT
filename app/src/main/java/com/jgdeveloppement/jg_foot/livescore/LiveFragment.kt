package com.jgdeveloppement.jg_foot.livescore

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentLiveBinding
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.URL_LIVE_SCORE
import com.jgdeveloppement.jg_foot.webview.MyWebViewClient


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

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = (activity as AppCompatActivity?)!!.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        Utils.unSelectBottomNavigationItem(bottomNavigationView, true)
        bottomNavigationView.menu.getItem(1).isChecked = true
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.liveScoreView.webViewClient = context?.let { MyWebViewClient(it) }
        binding.liveScoreView.loadUrl(URL_LIVE_SCORE)
        binding.liveScoreView.settings.allowContentAccess = true
        binding.liveScoreView.settings.javaScriptEnabled = true

    }


}
