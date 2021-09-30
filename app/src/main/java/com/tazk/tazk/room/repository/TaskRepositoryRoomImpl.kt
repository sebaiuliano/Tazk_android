package com.tazk.tazk.room.repository

import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.repository.TaskRepository
import com.tazk.tazk.room.dao.TaskDAO

class TaskRepositoryRoomImpl(
    private val taskDAO: TaskDAO
) : TaskRepository {
    override suspend fun insert(task: Task): Boolean {
        return taskDAO.insert(task) != 0L
    }

    override suspend fun insertAll(tasks: List<Task>): Boolean {
        return !taskDAO.insertAll(tasks).any { it == 0L }
    }

    override suspend fun update(task: Task): Boolean {
        taskDAO.update(task)
        return true
    }

    override suspend fun delete(task: Task): Boolean {
        taskDAO.delete(task)
        return true
    }

    override suspend fun getAll(): List<Task> {
        return taskDAO.getAll()
    }
}