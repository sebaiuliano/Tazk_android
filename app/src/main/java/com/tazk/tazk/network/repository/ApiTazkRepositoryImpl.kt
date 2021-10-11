package com.tazk.tazk.network.repository

import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.repository.ApiTazkRepository
import com.tazk.tazk.repository.TaskRepository
import retrofit2.Response
import java.io.IOException
import java.lang.RuntimeException

class ApiTazkRepositoryImpl(
    private val apiTazk: ApiTazk,
    private val taskRepository: TaskRepository
) : ApiTazkRepository {

    companion object {
        private var idToken: String = ""
    }

    override suspend fun signIn(token: String): Response<BasicResponse> {
        idToken = token
        return apiTazk.signIn(idToken)
    }

    override suspend fun createTask(task: Task): Boolean {
        return try {
            apiTazk.createTask(idToken, task).isSuccessful
        } catch(e: IOException) {
            taskRepository.insert(task)
        }
    }

    override suspend fun updateTask(task: Task): Boolean {
        return apiTazk.updateTask(idToken, task).isSuccessful
    }

    override suspend fun deleteTask(id: String): Boolean {
        return apiTazk.deleteTask(idToken, id).isSuccessful
    }

    override suspend fun getTasksByDate(
        startDate: String,
        endDate: String,
        category: String?
    ): Response<TasksResponse> {
        return apiTazk.getTasksByDate(idToken, startDate, endDate, category)
    }
}