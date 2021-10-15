package com.tazk.tazk.network.repository

import com.tazk.tazk.entities.network.request.DeleteImageRequest
import com.tazk.tazk.entities.network.request.ImageRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.network.response.ImageResponseWrapper
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.repository.ApiTazkRepository
import com.tazk.tazk.repository.TaskRepository
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.io.IOException

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

    override suspend fun uploadImage(file: File): Response<ImageResponseWrapper> {
        val requestBody = MultipartBody.Part.createFormData("image", file.name, RequestBody.create(MediaType.parse("image/*"), file))
        return apiTazk.uploadImage(idToken, requestBody)
    }

    override suspend fun deleteImage(deleteImageRequest: DeleteImageRequest): Response<BasicResponse> {
        return apiTazk.deleteImage(idToken, deleteImageRequest)
    }
}