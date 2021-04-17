package com.jgdeveloppement.jg_foot.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*

object Utils {
    const val ID_HOME = 1
    const val ID_EXPLORE = 2
    const val ID_NOTIFICATION = 3
    const val RC_SIGN_IN = 123
    const val RC_MATCH_URL = "RC_MATCH_URL"
    const val RC_MATCH_TITLE = "RC_MATCH_TITLE"
    const val RC_MATCH_ID = "RC_MATCH_ID"
    const val RC_FROM = "RC_FROM"
    const val TOP_MATCHES = "TOP_MATCHES"
    const val ALL_MATCHES = "ALL_MATCHES"
    const val LIGUE_1 = "FRANCE: Ligue 1"
    const val LEAGUE = "ENGLAND: Premier League"
    const val LIGA = "SPAIN: La Liga"
    const val SERIE_A = "ITALY: Serie A"
    const val URL_LIVE_SCORE = "https://www.scorebat.com/embed/livescore/"

    fun showSnackBar(view: View?, message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_SHORT).show()
    }

    fun isConnected(callback: (ok: Boolean) -> Unit){
        val command = "ping -c 1 google.com"
        val ping = Runtime.getRuntime().exec(command).waitFor()
        if (ping == 0) callback(true) else callback(false)
    }

    fun unSelectBottomNavigationItem(bottomNavigationView: BottomNavigationView, isCheckable: Boolean){
        bottomNavigationView.menu.setGroupCheckable(0, isCheckable, true)
    }

    fun setSelectedNavigationItem(id: Int, navigationView: NavigationView){
        when (id){
            0 -> {
                navigationView.menu.getItem(0).isChecked = true
                navigationView.menu.getItem(1).isChecked = false
                navigationView.menu.getItem(2).isChecked = false
                navigationView.menu.getItem(3).isChecked = false
            }
            1 -> {
                navigationView.menu.getItem(0).isChecked = false
                navigationView.menu.getItem(1).isChecked = true
                navigationView.menu.getItem(2).isChecked = false
                navigationView.menu.getItem(3).isChecked = false
            }
            2 -> {
                navigationView.menu.getItem(0).isChecked = false
                navigationView.menu.getItem(1).isChecked = false
                navigationView.menu.getItem(2).isChecked = true
                navigationView.menu.getItem(3).isChecked = false
            }
            3 -> {
                navigationView.menu.getItem(0).isChecked = false
                navigationView.menu.getItem(1).isChecked = false
                navigationView.menu.getItem(2).isChecked = false
                navigationView.menu.getItem(3).isChecked = true
            }
            else -> {
                navigationView.menu.getItem(0).isChecked = false
                navigationView.menu.getItem(1).isChecked = false
                navigationView.menu.getItem(2).isChecked = false
                navigationView.menu.getItem(3).isChecked = false
            }
        }
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun getUrl(string: String): String {
        var result = ""
        val regex = "https[^\"]*frameborder"
        val scan = Scanner(string)
        val find: String? = scan.findInLine(regex)
        if (find != null) {
            result = find.substring(0, find.length - 13)
        }
        return result
    }
}