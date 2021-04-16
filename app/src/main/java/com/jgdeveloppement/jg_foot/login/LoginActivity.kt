package com.jgdeveloppement.jg_foot.login

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.databinding.ActivityLoginBinding
import com.jgdeveloppement.jg_foot.home.HomeActivity
import com.jgdeveloppement.jg_foot.injection.Injection
import com.jgdeveloppement.jg_foot.models.User
import com.jgdeveloppement.jg_foot.utils.Utils
import com.jgdeveloppement.jg_foot.utils.Utils.RC_SIGN_IN
import com.jgdeveloppement.jg_foot.viewmodel.MainViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViewModel()
        onClickEmailLoginButton()
        onClickGoogleLoginButton()
    }

    override fun onResume() {
        super.onResume()
        rooting()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProviders.of(this, Injection.provideMainViewModelFactory()).get(MainViewModel::class.java)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginLayout(true)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                insertUserToFirebase(FirebaseAuth.getInstance().currentUser)
                HomeActivity.navigate(this)
            }else{
                when (response?.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> Utils.showSnackBar(binding.loginLayout, getString(R.string.error_no_internet))
                    ErrorCodes.UNKNOWN_ERROR -> Utils.showSnackBar(binding.loginLayout, getString(R.string.error_unknown_error))
                    else -> Utils.showSnackBar(binding.loginLayout, getString(R.string.error_authentication_canceled))
                }
            }

        }else{
            Utils.showSnackBar(binding.loginLayout, getString(R.string.error_authentication_canceled))
        }
    }

    /** Rooting  */
    private fun rooting() {
        loginLayout(false)
        Handler().postDelayed({
            Utils.isConnected { ok ->
                if (ok){
                    if (FirebaseAuth.getInstance().currentUser != null) HomeActivity.navigate(this)
                    else loginLayout(true)
                }
                else{
                    loginLayout(true)
                    Utils.showSnackBar(binding.loginLayout, getString(R.string.error_no_internet))
                }
            }
        }, (3 * 1000).toLong())
    }

    /** Login with Google  */
    private fun onClickGoogleLoginButton(){
        binding.loginButtonGoogle.setOnClickListener {
            startSignInActivity(AuthUI.IdpConfig.GoogleBuilder().build())
        }
    }

    /** Login with Mail  */
    private fun onClickEmailLoginButton(){
        binding.loginButtonMail.setOnClickListener {
            startSignInActivity(AuthUI.IdpConfig.EmailBuilder().build())
        }
    }

    private fun insertUserToFirebase(currentUser: FirebaseUser?) {
        if (currentUser != null){
            val id = currentUser.uid
            val name = if (currentUser.displayName != null) currentUser.displayName else "none"
            val avatarUrl = if (currentUser.photoUrl != null) currentUser.photoUrl.toString() else "none"
            val user = User(id, name , avatarUrl)
            mainViewModel.addUser(user)
        }

    }

    /** Login with FirebaseUI  */
    private fun startSignInActivity(authUI: AuthUI.IdpConfig) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(listOf(authUI))
                .setIsSmartLockEnabled(false, true)
                .build(),
            RC_SIGN_IN
        )
        loginLayout(false)
    }

    private fun loginLayout(visibility: Boolean){
        if (visibility) binding.loginProgressLayout.visibility = View.GONE
        else binding.loginProgressLayout.visibility = View.VISIBLE
    }

    companion object {
        /** Used to navigate to this activity  */
        fun navigate(activity: FragmentActivity?) {
            val intent = Intent(activity, LoginActivity::class.java)
            ActivityCompat.startActivity(activity!!, intent, null)
            activity.finish()
        }
    }
}
