package com.tazk.tazk.network.repository

import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.repository.ApiTazkRepository
import retrofit2.Response

class ApiTazkRepositoryImpl(
    private val apiTazk: ApiTazk
) : ApiTazkRepository {

    companion object {
        private var idToken: String = ""
    }

    override suspend fun signIn(token: String): Response<BasicResponse> {
        idToken = token
        return apiTazk.signIn(idToken)
    }

    override suspend fun createTask(task: Task): Response<BasicResponse> {
        return apiTazk.createTask(idToken, task)
    }

    override suspend fun updateTask(task: Task): Response<BasicResponse> {
        return apiTazk.updateTask(idToken, task)
    }

    override suspend fun deleteTask(id: String): Response<BasicResponse> {
        return apiTazk.deleteTask(idToken, id)
    }

    override suspend fun getTasksByDate(
        startDate: String,
        endDate: String
    ): Response<TasksResponse> {
        return apiTazk.getTasksByDate(idToken, startDate, endDate)
    }
}