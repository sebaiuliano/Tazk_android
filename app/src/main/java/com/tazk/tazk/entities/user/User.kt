package com.tazk.tazk.entities.user

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User (
    var email: String,
)