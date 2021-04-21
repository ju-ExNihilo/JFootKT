package com.jgdeveloppement.jg_foot.notification

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentNotificationBinding
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.Notification
import com.jgdeveloppement.jg_foot.reply.ReplyListActivity
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.showSnackBar
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel

class NotificationFragment : Fragment(), NotificationAdapter.OnNotificationClicked{

    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var adapter: NotificationAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        initNotificationList()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = (activity as AppCompatActivity?)!!.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        Utils.unSelectBottomNavigationItem(bottomNavigationView, true)
        bottomNavigationView.menu.getItem(2).isChecked = true
        mainViewModel.updateNotification(getUserId())
    }

    override fun onStart() {
        super.onStart()
        adapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        adapter!!.stopListening()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private fun initNotificationList(){
        val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
        binding.notificationRecyclerView.animation = fadeIn
        adapter = NotificationAdapter(context, this, mainViewModel.getLiveNotifications(getUserId()))
        binding.notificationRecyclerView.adapter = adapter
    }

    override fun onClickedDelete(notificationId: String) {
       mainViewModel.deleteNotification(getUserId(), notificationId)
    }

    override fun onClickedItem(notification: Notification, imageTransition: View) {
        mainViewModel.getComment(notification.commentId).observe(viewLifecycleOwner, {
            if (it != null){
                ReplyListActivity.navigate(activity, it, imageTransition)
            }else{
                showSnackBar(binding.notificationFragmentLayout, "Désolé le message à était supprimé")
            }
        })
    }

    private fun getUserId(): String = FirebaseAuth.getInstance().currentUser!!.uid

}
