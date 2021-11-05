package com.tazk.tazk.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.room.dao.TaskDAO
import com.tazk.tazk.util.moshiconverters.DateStringConverter

@Database(entities = [
    Task::class,
],
    version = 1, exportSchema = false)
@TypeConverters(
    DateStringConverter::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDAO() : TaskDAO
}