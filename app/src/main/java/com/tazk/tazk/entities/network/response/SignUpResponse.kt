package com.tazk.tazk.entities.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignUpResponse (
    var msg: String
)