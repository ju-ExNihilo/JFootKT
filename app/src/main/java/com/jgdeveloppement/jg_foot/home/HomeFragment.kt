package com.jgdeveloppement.jg_foot.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentHomeBinding
import com.jgdeveloppement.jg_foot.details.DetailsActivity
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.models.match.Match
import com.jgdeveloppement.jg_foot.utils.Status
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.ALL_MATCHES
import com.jgdeveloppement.jg_foot.utils.Utils.TOP_MATCHES
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel

class HomeFragment : Fragment(), MatchAdapter.OnCardMatchClicked {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainViewModel: MainViewModel
    private var navController: NavController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setupViewModel()
        initMatches()
        val lengt = "un commentaire un peu long pour tester comment cela rend sur le layout je vais jute rajouter un peu plus de text"
        Log.i("DEBUGGG", "lenght = ${lengt.length}")

        binding.topMatchNextButton.setOnClickListener { HomeActivity.navigateToMatchesList(TOP_MATCHES, navController) }
        binding.otherMatchNextButton.setOnClickListener { HomeActivity.navigateToMatchesList(ALL_MATCHES, navController) }
    }

    override fun onResume() {
        super.onResume()
        val navigationView = (activity as AppCompatActivity?)!!.findViewById<NavigationView>(R.id.nav_view)
        val bottomNavigationView = (activity as AppCompatActivity?)!!.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        Utils.setSelectedNavigationItem(4, navigationView)
        Utils.unSelectBottomNavigationItem(bottomNavigationView, true)
        bottomNavigationView.menu.getItem(0).isChecked = true
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    private fun initMatches(){
        mainViewModel.getAllMatches().observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.homeProgressLayout.visibility = View.GONE
                        resource.data?.let { matches -> initMatchesCard(matches) }
                    }
                    Status.ERROR -> {
                        binding.homeProgressLayout.visibility = View.GONE
                        Utils.showSnackBar(binding.homeFragmentLayout, getString(R.string.error_occurred))
                    }
                    Status.LOADING -> {
                        binding.homeProgressLayout.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun initMatchesCard(matches: List<Match>) {
        val topMatchArray = resources.getStringArray(R.array.top_match_query)
        val allMatch = matches.take(10)
        var topMatch = matches.filter{ match -> topMatchArray.contains(match.side1.name) ||  topMatchArray.contains(match.side2.name)}

        if (topMatch.size > 10) topMatch = topMatch.take(10)

        binding.topMatchRecyclerView.adapter = MatchAdapter(context as HomeActivity, topMatch.sortedByDescending { it.date }, R.layout.match_item, this)
        binding.otherMatchRecyclerView.adapter = MatchAdapter(context as HomeActivity, allMatch.sortedByDescending { it.date }, R.layout.match_item, this)
    }

    override fun onClickedMatch(matchUrl: String, matchTitle: String, matchId: String) {
        DetailsActivity.navigate(activity, matchUrl, matchTitle, matchId, binding.homeImage)
    }

}
