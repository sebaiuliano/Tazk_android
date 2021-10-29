package com.tazk.tazk.repository

import com.tazk.tazk.entities.network.request.DeleteImageRequest
import com.tazk.tazk.entities.network.request.ImageRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.network.response.ImageResponseWrapper
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import retrofit2.Response
import java.io.File

interface ApiTazkRepository {
    suspend fun signIn(token: String, registrationToken: String) : Response<BasicResponse>
    suspend fun createTask(task: Task) : Boolean
    suspend fun updateTask(task: Task) : Boolean
    suspend fun deleteTask(id: String) : Boolean
    suspend fun getTasksByDate(startDate: String, endDate: String, category: String?) : Response<TasksResponse>
    suspend fun uploadImage(file: File): Response<ImageResponseWrapper>
    suspend fun deleteImage(deleteImageRequest: DeleteImageRequest): Response<BasicResponse>
}