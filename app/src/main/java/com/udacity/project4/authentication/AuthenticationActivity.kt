package com.udacity.project4.authentication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.udacity.project4.R
import com.udacity.project4.databinding.ActivityAuthenticationBinding
import com.google.firebase.auth.FirebaseAuth
import com.udacity.project4.locationreminders.RemindersActivity
import java.security.AccessController.getContext

/**
 * This class should be the starting point of the app, It asks the users to sign in / register, and redirects the
 * signed in users to the RemindersActivity.
 */
class AuthenticationActivity : AppCompatActivity() {

    companion object {
        const val SIGN_UN_RESULT_CODE = 1001
    }

    private val viewModel by viewModels<LoginViewModel>()
    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.loginBt.setOnClickListener {
            launchSignFlow()
        }
        observeAuthState()
    }


    private fun observeAuthState(){

        viewModel.authenticationState.observe(this){ authState ->
            when(authState) {
                LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    // goToLocationReminders()
                    Log.i("jure","Login successfully")
                    binding.loginBt.text = "Logout"
                    binding.loginBt.setOnClickListener {
                        AuthUI.getInstance().signOut(this)
                    }
                    startLocationReminder()

                }
                else -> {
                    Log.i("jure", "login not successfully")
                }
            }

        }

    }

    private fun launchSignFlow() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
            AuthenticationActivity.SIGN_UN_RESULT_CODE
        )

    }

    private fun startLocationReminder(){
        val intent = Intent(this, RemindersActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if( requestCode == SIGN_UN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                Log.i(
                    "jure",
                    "Successfully signed in user ${FirebaseAuth.getInstance().currentUser?.displayName}!"
                )
            } else {
                Log.i("jure", "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

}
