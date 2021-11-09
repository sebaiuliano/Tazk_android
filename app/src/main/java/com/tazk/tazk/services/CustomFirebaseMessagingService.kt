package com.tazk.tazk.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.tazk.tazk.application.modules.apiModule
import com.tazk.tazk.application.modules.apiRepositoryModule
import com.tazk.tazk.application.modules.networkModule
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.network.repository.ApiTazkRepositoryImpl
import com.tazk.tazk.repository.ApiTazkRepository
import kotlinx.coroutines.*
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.android.inject
import retrofit2.Call
import timber.log.Timber

class CustomFirebaseMessagingService: FirebaseMessagingService() {

    val apiTazkRepository : ApiTazkRepository by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("NEW REGISTRATIONTOKEN: $token")
        try {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    apiTazkRepository.updateRegistrationToken(token)
                }
            }
        } catch(e: Exception) {
            Timber.e(e)
        }
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        println("NEW NOTIFICATION: ${msg.notification?.body}")
    }
}