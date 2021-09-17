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
    @POST("signup")
    suspend fun createUser(@Body user: User) : Response<BasicResponse>
    @POST("/tazk/create")
    suspend fun createTask(@Body taskRequest: TaskRequest) : Response<BasicResponse>
    @PUT("/tazk/update")
    suspend fun updateTask(@Body taskRequest: TaskRequest) : Response<BasicResponse>
    @DELETE("/tazk")
    suspend fun deleteTask(@Query("id") id: String) : Response<BasicResponse>
    @GET("/tazk/getByDate")
    suspend fun getTasksByDate(
        @Query("email") email: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ) : Response<TasksResponse>
}