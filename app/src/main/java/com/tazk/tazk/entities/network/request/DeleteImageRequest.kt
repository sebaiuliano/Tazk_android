package com.tazk.tazk.entities.network.request

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class DeleteImageRequest (
    var publicId: String
)