package com.tazk.tazk.application.modules.entities

import com.tazk.tazk.repository.TaskRepository
import com.tazk.tazk.room.AppDatabase
import com.tazk.tazk.room.repository.TaskRepositoryRoomImpl
import org.koin.dsl.module

val taskModule = module {
    single<TaskRepository>(override = true) {
        TaskRepositoryRoomImpl(
            get()
        )
    }
    single { get<AppDatabase>().taskDAO() }
}