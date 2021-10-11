package com.tazk.tazk.application.modules

import com.tazk.tazk.network.endpoint.ApiTazk
import com.tazk.tazk.network.repository.ApiTazkRepositoryImpl
import com.tazk.tazk.repository.ApiTazkRepository
import com.tazk.tazk.repository.TaskRepository
import com.tazk.tazk.room.repository.TaskRepositoryRoomImpl
import org.koin.dsl.module

val apiRepositoryModule = module {
    fun provideApiTazkRepository(
        apiTazk: ApiTazk,
        taskRepository: TaskRepository
    ): ApiTazkRepository {
        return ApiTazkRepositoryImpl(apiTazk, taskRepository)
    }

    single { provideApiTazkRepository(get(), get()) }
}