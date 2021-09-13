package com.tazk.tazk.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    fun onNewTaskClick() {
        //TODO crear nueva tarea
    }

    fun deleteTask() {
        //TODO eliminar tarea
    }
}