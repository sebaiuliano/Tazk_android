package com.tazk.tazk.entities.network.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tazk.tazk.util.moshiconverters.FechaString
import java.util.*

@JsonClass(generateAdapter = true)
data class TaskRequest (
    @Json(name="_id")
    var id : String?,
    var email: String,
    var title: String,
    var description: String,
    @Json(name="dateCreated")
    @FechaString
    var createdAt: GregorianCalendar,
    var category: String?
)