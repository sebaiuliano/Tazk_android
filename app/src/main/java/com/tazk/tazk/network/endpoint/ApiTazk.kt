package com.tazk.tazk.network.endpoint

import com.tazk.tazk.entities.network.request.DeleteImageRequest
import com.tazk.tazk.entities.network.request.ImageRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.network.response.ImageResponseWrapper
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import java.io.File

interface ApiTazk {
    @POST("/signInOrSignUp")
    suspend fun signIn(
        @Header("idToken") idToken: String,
        @Header("registrationToken") registrationToken: String
    ) : Response<BasicResponse>
    @POST("/tazk")
    suspend fun createTask(@Header("idToken") idToken: String, @Body task: Task) : Response<BasicResponse>
    @PUT("/tazk")
    suspend fun updateTask(@Header("idToken") idToken: String, @Body task: Task) : Response<BasicResponse>
    @DELETE("/tazk")
    suspend fun deleteTask(@Header("idToken") idToken: String, @Query("id") id: String) : Response<BasicResponse>
    @GET("/tazk/getByDate")
    suspend fun getTasksByDate(
        @Header("idToken") idToken: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String,
        @Query("category") category: String?
    ) : Response<TasksResponse>
    @Multipart
    @POST("/tazk/image")
    suspend fun uploadImage(
        @Header("idToken") idToken: String,
        @Part image: MultipartBody.Part
    ): Response<ImageResponseWrapper>
    @HTTP(method = "DELETE", path = "/tazk/image", hasBody = true)
    suspend fun deleteImage(@Header("idToken") idToken: String, @Body deleteImageRequest: DeleteImageRequest): Response<BasicResponse>
}