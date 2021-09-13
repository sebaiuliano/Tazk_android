package com.tazk.tazk.entities.task

import com.squareup.moshi.JsonClass
import java.util.*

@JsonClass(generateAdapter = true)
data class Task (
    var title: String,
    var description: String,
    var createdAt: GregorianCalendar,
    var modifiedAt: GregorianCalendar,
    var deleted: Boolean,
) {
    constructor(title: String, description: String) : this(title, description, GregorianCalendar(), GregorianCalendar(), false)
}