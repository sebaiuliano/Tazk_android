package com.tazk.tazk.network

import com.tazk.tazk.entities.network.response.BasicResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.user.User
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ApiLogin {
    @POST("signup")
    fun createUser(@Body user: User) : Call<BasicResponse>
    @POST("/tazk/create")
    fun createTask(@Body task: Task) : Call<BasicResponse>
    @PUT("/tazk/update")
    fun updateTask(@Body task: Task) : Call<BasicResponse>
    @DELETE("/tazk")
    fun deleteTask(@Query("id") id: String) : Call<BasicResponse>
    @GET("/tazk/getByDate")
    fun getTasksByDate(
        @Query("email") email: String,
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ) : Call<BasicResponse>



    companion object {

        var BASE_URL = "https://tazk-app.herokuapp.com/"
        var BASE_URL_TEST = "http://10.0.2.2:3000/"

        fun create() : ApiLogin {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiLogin::class.java)
        }
    }
}