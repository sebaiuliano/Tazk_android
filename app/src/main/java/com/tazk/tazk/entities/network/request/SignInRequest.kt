package com.tazk.tazk.entities.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignInRequest (
    @Json(name="registration_token")
    var registrationToken : String
)