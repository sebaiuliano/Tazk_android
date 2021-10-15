package com.tazk.tazk.entities.network.request

import com.squareup.moshi.JsonClass
import java.io.File

@JsonClass(generateAdapter = true)
data class ImageRequest (
    var image: File
)