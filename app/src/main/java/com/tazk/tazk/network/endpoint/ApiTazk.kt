package com.tazk.tazk.network.endpoint

import com.tazk.tazk.entities.network.request.TaskRequest
import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ApiTazk {
    @POST("/signInOrSignUp")
    suspend fun signIn(@Header("idToken") idToken:String) : Response<BasicResponse>
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
}