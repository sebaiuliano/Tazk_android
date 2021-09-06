package com.tazk.tazk.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityLoginBinding
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.ApiLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

//    private val model: LoginViewModel by viewModel()
    private lateinit var binding : ActivityLoginBinding
    private var RC_SIGN_IN = 1
    private lateinit var apiLogin: ApiLogin

    private var signupResponseMutableHandler : MutableLiveData<Boolean> = MutableLiveData()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        apiLogin = ApiLogin.create()
        setObservers()

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            successfulLogin(account)
        }
        // Set the dimensions of the sign-in button
        binding.signInButton.setSize(SignInButton.SIZE_STANDARD)
        binding.signInButton.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.result
            successfulLogin(account)
            // Signed in successfully, show authenticated UI.
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.e("signInResult:failed code= ${e.statusCode}")
            failedLogin()
        }
    }

    private fun setObservers() {
        signupResponseMutableHandler.observe(this) {
            if (it) {
                apiSignupSuccess()
            } else {
                apiSignupFailure()
            }
        }
    }

    private fun successfulLogin(account: GoogleSignInAccount) {
        CoroutineScope(Dispatchers.IO).launch {
            account.email?.let { email ->
                val response = apiLogin.createUser(
                    User(
                        email
                    )
                ).execute()
                println("RESULTADO DE REQUEST: ${response.isSuccessful} - ${response.body()}")
                if (response.isSuccessful) {
                    signupResponseMutableHandler.postValue(true)
                } else {
                    signupResponseMutableHandler.postValue(false)
                }
            }
        }
    }

    private fun failedLogin() {
        Toast.makeText(this, "Fallo el login", Toast.LENGTH_SHORT).show()
    }

    private fun apiSignupSuccess() {
        Toast.makeText(this, "Registrado correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun apiSignupFailure() {
        Toast.makeText(this, "Error al registrar usuario en el sistema", Toast.LENGTH_SHORT).show()
    }
}