package com.tazk.tazk.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.repository.ApiTazkRepository
import kotlinx.coroutines.*
import timber.log.Timber

class LoginViewModel(
    private val apiTazkRepository: ApiTazkRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var signupResponseMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var signInResponseMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    fun successfulGoogleLogin(account: GoogleSignInAccount) {
        uiScope.launch {
            account.email?.let { email ->
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.createUser(User(email))
                }
                Timber.d("LOGIN REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
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