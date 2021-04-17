package com.jgdeveloppement.jg_foot.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.badge.BadgeDrawable
import com.firebase.ui.auth.AuthUI
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.navigation.NavigationView
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityHomeBinding
import com.jgdeveloppement.jg_foot.login.LoginActivity
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.hideKeyboard
import com.miguelcatalan.materialsearchview.MaterialSearchView

class HomeActivity : AppCompatActivity(), MaterialSearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private var navController: NavController? = null
    var notificationsBadge : View?  = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setSupportActionBar(binding.toolbar)
        configureDrawerLayout()

        binding.navView.setNavigationItemSelectedListener(this)
        binding.bottomNavigationView.setupWithNavController(navController!!)

        addBadge("2")
    }

    private fun getBadge() : View {
        if (notificationsBadge != null){
            return notificationsBadge!!
        }
        val mbottomNavigationMenuView = binding.bottomNavigationView.getChildAt(2) as BottomNavigationMenuView
        notificationsBadge = LayoutInflater.from(this).inflate(R.layout.custom_badge_layout,
                mbottomNavigationMenuView,false)
        return notificationsBadge!!
    }

    private fun addBadge(count : String) {
        val badge: BadgeDrawable = binding.bottomNavigationView.getOrCreateBadge(R.id.messageFragment)
        badge.number = 3
        badge.backgroundColor = resources.getColor(R.color.colorGold, null)
        badge.badgeTextColor = resources.getColor(R.color.colorWhite, null)
        badge.isVisible = true
    }

    //  Configure Drawer Layout
    private fun configureDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, binding.layoutDrawer, binding.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        binding.layoutDrawer.addDrawerListener(toggle)
        toggle.syncState()
    }


    //  Configure ToolBar with badge
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        val item = menu?.findItem(R.id.action_search)
        binding.searchView.setMenuItem(item)
        binding.searchView.setCursorDrawable(R.drawable.custom_cursor)
        binding.searchView.setSuggestions(resources.getStringArray(R.array.top_match_query))
        binding.searchView.setOnQueryTextListener(this)


        return true
    }

    override fun onBackPressed() {
        when{
            binding.layoutDrawer.isDrawerOpen(GravityCompat.START) -> binding.layoutDrawer.closeDrawer(GravityCompat.START)
            binding.searchView.isSearchOpen -> binding.searchView.closeSearch()
            else -> super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.ligue_1 -> {
                navigateToMatchesList(Utils.LIGUE_1, navController)
            }
            R.id.premier_league -> {
                navigateToMatchesList(Utils.LEAGUE, navController)
            }
            R.id.liga -> {
                navigateToMatchesList(Utils.LIGA, navController)
            }
            R.id.serie_a -> {
                navigateToMatchesList(Utils.SERIE_A, navController)
            }
            R.id.logout -> {
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnSuccessListener { finish(); LoginActivity.navigate(this) }
            }
            else -> return false

        }

        binding.layoutDrawer.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onQueryTextSubmit(query: String?): Boolean {
        applicationContext.hideKeyboard(binding.layoutDrawer)
        binding.searchView.closeSearch()
        query?.let { navigateToMatchesList(it, navController) }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?) {
            val intent = Intent(activity, HomeActivity::class.java)
            ActivityCompat.startActivity(activity!!, intent, null)
            activity.finish()
        }

        fun navigateToMatchesList(from: String, navController: NavController?){
            val navOptions: NavOptions = NavOptions.Builder().setPopUpTo(R.id.matchesFragment, true).build()
            val bundle = Bundle()
            bundle.putString(Utils.RC_FROM, from)
            navController?.navigate(R.id.matchesFragment, bundle, navOptions)
        }
    }
}
