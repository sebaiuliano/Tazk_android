package com.tazk.tazk.entities.network.response

import com.squareup.moshi.JsonClass
import com.tazk.tazk.entities.task.Task

@JsonClass(generateAdapter = true)
data class TasksResponse (
    var data: List<Task>
)