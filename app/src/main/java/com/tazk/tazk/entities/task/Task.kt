package com.tazk.tazk.entities.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.util.moshiconverters.FechaString
import java.util.*

@JsonClass(generateAdapter = true)
@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name="id_local")
    @Transient
    var idLocal: Long = 0L,
    @Json(name="_id")
    var id: String?,
    var title: String,
    var description: String,
    @Json(name="dateCreated")
    @ColumnInfo(name = "created_at")
    @FechaString
    var createdAt: GregorianCalendar,
    var category: String
) {
    @Ignore
    constructor(title: String, description: String) : this(
        0L,
        "",
        title,
        description,
        GregorianCalendar(),
        ""
    )

    @Ignore
    constructor(title: String, description: String, category: String) : this(
        0L,
        "",
        title,
        description,
        GregorianCalendar(),
        category
    )

    @Ignore
    constructor(id: String?, title: String, description: String, category: String) : this(
        0L,
        id,
        title,
        description,
        GregorianCalendar(),
        category
    )

    @Ignore
    constructor(id: String?, title: String, description: String, createdAt: GregorianCalendar, category: String) : this(
        0L,
        id,
        title,
        description,
        createdAt,
        category
    )
}

fun Task.toTaskRequest() : TaskRequest {
    return TaskRequest(
        id,
        "",
        title,
        description,
        createdAt,
        if (category.isNotEmpty()) { category } else { null }
    )
}