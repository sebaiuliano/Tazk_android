package com.tazk.tazk.room.dao

import androidx.room.*
import com.tazk.tazk.entities.task.Task

@Dao
interface TaskDAO {
    @Insert
    fun insert(task: Task): Long
    @Insert
    fun insertAll(tasks: List<Task>): List<Long>
    @Update
    fun update(task: Task)
    @Delete
    fun delete(task: Task)
    @Query("SELECT * FROM tasks")
    fun getAll(): List<Task>
}