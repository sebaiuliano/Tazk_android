package com.tazk.tazk.repository

import com.tazk.tazk.entities.task.Task

interface TaskRepository {
    suspend fun insert(task: Task): Boolean
    suspend fun insertAll(tasks: List<Task>): Boolean
    suspend fun update(task: Task): Boolean
    suspend fun delete(task: Task): Boolean
    suspend fun getAll(): List<Task>
}