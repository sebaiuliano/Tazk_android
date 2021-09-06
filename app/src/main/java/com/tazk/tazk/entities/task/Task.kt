package com.tazk.tazk.entities.task

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Task (
    var title: String,
    var createdAt: GregorianCalendar,
    var modifiedAt: GregorianCalendar,
    var deleted: Boolean,
)