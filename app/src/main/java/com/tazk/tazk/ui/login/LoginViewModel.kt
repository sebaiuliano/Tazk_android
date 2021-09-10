package com.tazk.tazk.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.ApiLogin
import kotlinx.coroutines.*

class LoginViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    lateinit var apiLogin: ApiLogin
    var signupResponseMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var signInResponseMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    fun successfulGoogleLogin(account: GoogleSignInAccount) {
        uiScope.launch {
            account.email?.let { email ->
                val response = withContext(Dispatchers.IO) {
                    apiLogin.createUser(
                        User(
                            email
                        )
                    ).execute()
                }
                println("RESULTADO DE REQUEST: ${response.isSuccessful} - ${response.body()}")
                if (response.isSuccessful) {
                    signupResponseMutableHandler.postValue(true)
                } else {
                    signupResponseMutableHandler.postValue(false)
                }
            }
        }
    }

    fun registeredUser(account: GoogleSignInAccount){
        //TODO pegarle a la api para hacer login
        signInResponseMutableHandler.postValue(true)
    }
}