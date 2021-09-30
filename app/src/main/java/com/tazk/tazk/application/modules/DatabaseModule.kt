package com.tazk.tazk.application.modules

import androidx.room.Room
import com.tazk.tazk.room.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, "task-db").fallbackToDestructiveMigration().build()
    }
}