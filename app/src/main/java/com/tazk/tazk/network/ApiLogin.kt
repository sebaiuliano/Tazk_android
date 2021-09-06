package com.tazk.tazk.network

import com.squareup.moshi.Moshi
import com.tazk.tazk.entities.network.response.SignUpResponse
import com.tazk.tazk.entities.user.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface ApiLogin {
    @POST("signup")
    fun createUser(@Body user: User) : Call<SignUpResponse>

    companion object {

        var BASE_URL = "https://tazk-app.herokuapp.com/"

        fun create() : ApiLogin {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(ApiLogin::class.java)
        }
    }
}