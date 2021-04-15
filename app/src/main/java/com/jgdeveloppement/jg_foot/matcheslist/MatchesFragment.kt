package com.jgdeveloppement.jg_foot.matcheslist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.FragmentMatchesBinding
import com.jgdeveloppement.jg_foot.details.DetailsActivity
import com.jgdeveloppement.jg_foot.home.HomeActivity
import com.jgdeveloppement.jg_foot.home.MatchAdapter
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.Match
import com.jgdeveloppement.jg_foot.utils.Status
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.viewmodel.MatchViewModel


class MatchesFragment : Fragment(), MatchAdapter.OnCardMatchClicked {

    private var _binding: FragmentMatchesBinding? = null
    private val binding get() = _binding!!
    private lateinit var matchViewModel: MatchViewModel
    private var from: String? = null
    private var navController: NavController? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMatchesBinding.inflate(inflater, container, false)
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
        setHasOptionsMenu(true)
        if (arguments != null){
            from = requireArguments().getString(Utils.RC_FROM)
            initMatches()
        }

        // Back button press
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navController?.navigateUp()
        }

    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = (activity as AppCompatActivity?)!!.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
        Utils.unSelectBottomNavigationItem(bottomNavigationView, false)
    }

    private fun setupViewModel() {
        matchViewModel = ViewModelProviders.of(this, Injection.provideMatchViewModelFactory()).get(MatchViewModel::class.java)
    }

    private fun initMatches(){
        matchViewModel.getAllMatches().observe(viewLifecycleOwner, {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        binding.matchesProgressLayout.visibility = View.GONE
                        resource.data?.let { matches -> initMatchesCard(matches) }
                    }
                    Status.ERROR -> {
                        binding.matchesProgressLayout.visibility = View.GONE
                        Utils.showSnackBar(binding.matchesFragmentLayout, getString(R.string.error_occurred))
                    }
                    Status.LOADING -> {
                        binding.matchesProgressLayout.visibility = View.VISIBLE
                    }
                }
            }
        })
    }

    private fun initMatchesCard(matches: List<Match>) {
        val topMatchArray = resources.getStringArray(R.array.top_match_query)
        val topMatch = matches.filter{ match -> topMatchArray.contains(match.side1.name) || topMatchArray.contains(match.side2.name) }
        val ligue1 = matches.filter{ match -> match.competition.name.contentEquals(Utils.LIGUE_1)}
        val league = matches.filter{ match -> match.competition.name.contentEquals(Utils.LEAGUE)}
        val liga = matches.filter{ match -> match.competition.name.contentEquals(Utils.LIGA)}
        val serieA = matches.filter{ match -> match.competition.name.contentEquals(Utils.SERIE_A)}

        when(from){
            Utils.TOP_MATCHES -> initLayout("Top Matchs", R.drawable.top_match, topMatch)
            Utils.ALL_MATCHES -> initLayout("Matchs", R.drawable.ball, matches)
            Utils.LIGUE_1 -> {
                initLayout(setTitle(Utils.LIGUE_1), R.drawable.ic_ligue_1, ligue1)
                initSelectedItem(0)
            }
            Utils.LEAGUE -> {
                initLayout(setTitle(Utils.LEAGUE), R.drawable.ic_premier_league, league)
                initSelectedItem(1)
            }
            Utils.LIGA ->{
                initLayout(setTitle(Utils.LIGA), R.drawable.ic_liga, liga)
                initSelectedItem(2)
            }
            Utils.SERIE_A ->{
                initLayout(setTitle(Utils.SERIE_A), R.drawable.ic_serie_a, serieA)
                initSelectedItem(3)
            }
            else -> {
                val queryResult = matches.filter{ match -> match.title.contains(from!!, true)}
                initLayout("Recherche", R.drawable.ball, queryResult)
            }
        }

    }

    private fun initLayout(title: String, drawableId: Int, matches: List<Match>){
        if (matches.isNotEmpty()){
            val slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
            binding.matchesRecyclerView.animation = slideUp
            binding.matchesRecyclerView.visibility = View.VISIBLE
            binding.errorTextView.visibility = View.GONE
            binding.matchesTitle.text = title
            binding.matchesImageTitle.setImageDrawable(resources.getDrawable(drawableId, null))
            binding.matchesRecyclerView.adapter = MatchAdapter(context as HomeActivity, matches, R.layout.matches_item, this)
        }else{
            binding.matchesRecyclerView.visibility = View.GONE
            binding.errorTextView.visibility = View.VISIBLE
        }

    }

    private fun setTitle(string: String) : String{
        val index = string.lastIndexOf(':')
        return string.substring(index + 1)
    }

    private fun initSelectedItem(index: Int) {
        val navigationView = (activity as AppCompatActivity?)!!.findViewById<NavigationView>(R.id.nav_view)
        Utils.setSelectedNavigationItem(index, navigationView)
    }

    override fun onClickedMatch(matchUrl: String, matchTitle: String) {
        DetailsActivity.navigate(activity, matchUrl, matchTitle)
    }

}
