package com.tazk.tazk.repository

import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import retrofit2.Response

interface ApiTazkRepository {
    suspend fun signIn(token: String) : Response<BasicResponse>
    suspend fun createTask(task: Task) : Response<BasicResponse>
    suspend fun updateTask(task: Task) : Response<BasicResponse>
    suspend fun deleteTask(id: String) : Response<BasicResponse>
    suspend fun getTasksByDate(startDate: String, endDate: String) : Response<TasksResponse>
}