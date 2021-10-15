package com.tazk.tazk.entities.network.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ImageResponse (
    var publicId: String,
    var url: String
)