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
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityLoginBinding
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.ui.main.MainActivity
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

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
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            model.registeredUser(it)
        }
        binding.signInButton.setOnClickListener {
            if (account == null) {
                goSignIn()
            } else {
                alreadyLogged()
            }
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
                model.successfulGoogleLogin(it)
            }
        } catch (e: ApiException) {
            Timber.e("signInResult:failed code= ${e.statusCode}")
            failedLogin()
        }
    }

    private fun setObservers() {
        model.signupResponseMutableHandler.observe(this) {
            if (it) {
                apiSignupSuccess()
            } else {
                apiSignupFailure()
            }
        }

        model.signInResponseMutableHandler.observe(this) {
            if (it) {
                model.signInResponseMutableHandler.postValue(false)
                apiSignInSuccess()
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

    private fun apiSignInSuccess() {
        Toast.makeText(this, "Logueado correctamente", Toast.LENGTH_SHORT).show()
        goMainActivity()
    }

    private fun alreadyLogged() {
        Toast.makeText(this, "Usuario ya logueado", Toast.LENGTH_SHORT).show()
    }

    private fun goMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}