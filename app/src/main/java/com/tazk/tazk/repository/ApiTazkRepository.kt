package com.tazk.tazk.repository

import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import retrofit2.Response

interface ApiTazkRepository {
    suspend fun createUser(user: User) : Response<BasicResponse>
    suspend fun createTask(taskRequest: TaskRequest) : Response<BasicResponse>
    suspend fun updateTask(taskRequest: TaskRequest) : Response<BasicResponse>
    suspend fun deleteTask(id: String) : Response<BasicResponse>
    suspend fun getTasksByDate(email: String, startDate: String, endDate: String) : Response<TasksResponse>
}