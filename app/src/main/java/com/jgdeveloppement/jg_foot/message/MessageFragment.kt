package com.jgdeveloppement.jg_foot.message

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentMessageBinding
import com.jgdeveloppement.jg_foot.utils.Utils

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
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
        bottomNavigationView.menu.getItem(2).isChecked = true
    }

}
