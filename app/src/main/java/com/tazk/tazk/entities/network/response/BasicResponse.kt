package com.tazk.tazk.entities.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BasicResponse (
    var msg: String
)