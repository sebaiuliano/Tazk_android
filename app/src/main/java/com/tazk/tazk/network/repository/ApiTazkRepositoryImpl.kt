package com.tazk.tazk.network.repository

import com.tazk.tazk.application.modules.Properties.BASE_URL
import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.repository.ApiTazkRepository
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class ApiTazkRepositoryImpl(
    private val apiTazk: ApiTazk
) : ApiTazkRepository {

    override suspend fun createUser(user: User): Response<BasicResponse> {
        return apiTazk.createUser(user)
    }

    override suspend fun createTask(taskRequest: TaskRequest): Response<BasicResponse> {
        return apiTazk.createTask(taskRequest)
    }

    override suspend fun updateTask(taskRequest: TaskRequest): Response<BasicResponse> {
        return apiTazk.updateTask(taskRequest)
    }

    override suspend fun deleteTask(id: String): Response<BasicResponse> {
        return apiTazk.deleteTask(id)
    }

    override suspend fun getTasksByDate(
        email: String,
        startDate: String,
        endDate: String
    ): Response<TasksResponse> {
        return apiTazk.getTasksByDate(email, startDate, endDate)
    }

}