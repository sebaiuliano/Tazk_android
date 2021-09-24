package com.tazk.tazk.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.repository.ApiTazkRepository
import kotlinx.coroutines.*
import timber.log.Timber

class LoginViewModel(
    private val apiTazkRepository: ApiTazkRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var signInSuccessMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var signInErrorMutableHandler = MutableLiveData<Boolean>()

    fun successfulGoogleLogin(account: GoogleSignInAccount) {
        uiScope.launch {
            account.idToken?.let { token ->
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.signIn(token)
                }
                Timber.d("LOGIN REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
                if (response.isSuccessful) {
                    signInSuccessMutableHandler.postValue(true)
                } else {
                    signInSuccessMutableHandler.postValue(false)
                }
            }
        }
    }
}