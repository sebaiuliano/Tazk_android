package com.tazk.tazk.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.internal.OnConnectionFailedListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityLoginBinding
import com.tazk.tazk.ui.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.RuntimeException


class LoginActivity : AppCompatActivity(), OnConnectionFailedListener {

    private val model: LoginViewModel by viewModel()
    private lateinit var binding : ActivityLoginBinding
    private var RC_SIGN_IN = 1
    private lateinit var mGoogleSignInClient : GoogleSignInClient
    private var account: GoogleSignInAccount? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginViewModel = model
        binding.lifecycleOwner = this

        setObservers()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("966205408454-jjtc7ij257qpej8u5gr0igbuncfcfg88.apps.googleusercontent.com")
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mGoogleSignInClient.silentSignIn().addOnCompleteListener(this) { task -> handleSignInResult(task) }

        binding.signInButton.setOnClickListener {
//            if (account == null) {
                goSignIn()
//            }
        }
    }

    private fun goSignIn(){
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            account = completedTask.result
            account?.let {
                println("IDTOKEN: ${it.idToken}")
                //De aca va a buscar el token de FCM
                getRegistrationToken()
            } ?: run {
                goSignIn()
            }
        } catch (e: ApiException) {
            Timber.e("signInResult:failed code= ${e.statusCode}")
//            failedLogin()
            goMainActivity()
        } catch (e: RuntimeException) {
            Timber.e("signInResult:failed code= ${e.message}")
            if ((e.cause as? ApiException)?.statusCode == 7) {
                goMainActivity()
            } else {
                failedLogin()
            }
        }
    }

    private fun setObservers() {
        model.signInSuccessMutableHandler.observe(this) {
            if (it) {
                model.signInSuccessMutableHandler.postValue(false)
                apiSignInSuccess()
            }
        }
        model.signInErrorMutableHandler.observe(this) {
            if (it) {
                model.signInErrorMutableHandler.postValue(false)
                apiSignInError()
            }
        }
        model.noInternetMutableHandler.observe(this) {
            if (it) {
                goMainActivity()
            }
        }
    }

    private fun failedLogin() {
        Toast.makeText(this, "Fallo el login", Toast.LENGTH_SHORT).show()
    }

    private fun apiSignInSuccess() {
        goMainActivity()
    }

    private fun apiSignInError() {
        Toast.makeText(this, "Ocurrió un error al iniciar sesión en la aplicación", Toast.LENGTH_SHORT).show()
    }

    private fun goMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        println("CONNECTION FAILED")
    }

    private fun getRegistrationToken() {
        //Seteo un listener para que, cuando vuelva el token, le mande el login al srv con toda la info
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Timber.e(task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            println("REGISTRATIONTOKEN: $token")
            model.successfulGoogleLogin(account!!, token)
        })
    }


}