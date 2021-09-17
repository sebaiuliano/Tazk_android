package com.tazk.tazk.application.modules

import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.network.repository.ApiTazkRepositoryImpl
import com.tazk.tazk.repository.ApiTazkRepository
import org.koin.dsl.module

val apiRepositoryModule = module {
    fun provideApiTazkRepository(
        apiTazk: ApiTazk
    ): ApiTazkRepository {
        return ApiTazkRepositoryImpl(apiTazk)
    }

    single { provideApiTazkRepository(get()) }
}