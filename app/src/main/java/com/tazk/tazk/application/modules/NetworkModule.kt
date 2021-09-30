package com.tazk.tazk.application.modules

import com.squareup.moshi.Moshi
import com.tazk.tazk.application.modules.Properties.BASE_URL
import com.tazk.tazk.util.interceptors.ConnectivityInterceptor
import com.tazk.tazk.util.moshiconverters.DateStringConverter
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object Properties {
    var BASE_URL = "https://tazk-app.herokuapp.com/"
    var BASE_URL_TEST = "http://10.0.2.2:3000/"
}

val networkModule = module {

    fun provideHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ConnectivityInterceptor())
            .build()
    }

    fun provideRetrofit(client: OkHttpClient, moshi: Moshi, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(client)
            .build()
    }

    fun provideMoshi() = Moshi.Builder()
        .add(DateStringConverter())
        .build()

    single (named("http")) {
        provideHttpClient()
    }

    single (named("api-tazk")) {
        provideRetrofit(
            get(named("http")),
            get(),
            BASE_URL
        )
    }

    single { provideMoshi() }

}

