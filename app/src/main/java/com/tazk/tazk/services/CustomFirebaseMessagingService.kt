package com.tazk.tazk.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class CustomFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        //TODO registrar el nuevo token
        println("NEW TOKEN: $token")
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)
        println(msg.notification?.body)
    }
}