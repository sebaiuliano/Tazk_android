package com.tazk.tazk.entities.task

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.util.moshiconverters.FechaString
import java.util.*

@JsonClass(generateAdapter = true)
data class Task (
    @Json(name="_id")
    var id: String?,
    var title: String,
    var description: String,
    @Json(name="dateCreated")
    @FechaString
    var createdAt: GregorianCalendar,
//    @FechaString
//    var modifiedAt: GregorianCalendar,
//    var deleted: Boolean,
) {
    //    constructor(title: String, description: String) : this("", title, description, GregorianCalendar(), GregorianCalendar(), false)
//    constructor(id: String, title: String, description: String) : this(id, title, description, GregorianCalendar(), GregorianCalendar(), false)
    constructor(title: String, description: String) : this(
        "",
        title,
        description,
        GregorianCalendar()
    )

    constructor(id: String, title: String, description: String) : this(
        id,
        title,
        description,
        GregorianCalendar()
    )
}

fun Task.toTaskRequest() : TaskRequest {
    return TaskRequest(
        id,
        "",
        title,
        description,
        createdAt
    )
}