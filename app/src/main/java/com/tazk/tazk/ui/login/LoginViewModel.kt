package com.tazk.tazk.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tazk.tazk.repository.ApiTazkRepository
import kotlinx.coroutines.*

class LoginViewModel(
    private val apiTazkRepository: ApiTazkRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var signInSuccessMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var signInErrorMutableHandler = MutableLiveData<Boolean>()
    var noInternetMutableHandler = MutableLiveData<Boolean>()

    fun successfulGoogleLogin(account: GoogleSignInAccount, registrationToken: String) {
        uiScope.launch {
            account.idToken?.let { token ->
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.signIn(token, registrationToken)
                }
                println("LOGIN REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
                when {
                    response.isSuccessful -> { signInSuccessMutableHandler.postValue(true) }
                    response.code() == 600 -> { noInternetMutableHandler.postValue(true) }
                    else -> { signInErrorMutableHandler.postValue(true) }
                }
            }
        }
    }
}