package com.tazk.tazk.application.modules

import com.tazk.tazk.network.endpoint.ApiTazk
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    fun provideApiTazk(retrofit: Retrofit): ApiTazk {
        return retrofit.create(ApiTazk::class.java)
    }

    single { provideApiTazk(get(named("api-tazk"))) }
}