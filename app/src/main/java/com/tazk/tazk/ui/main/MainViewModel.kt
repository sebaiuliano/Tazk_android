package com.tazk.tazk.ui.main

import android.os.Build
import android.text.format.DateUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.entities.task.toTaskRequest
import com.tazk.tazk.repository.ApiTazkRepository
import kotlinx.coroutines.*
import timber.log.Timber
import java.text.DateFormat.getDateInstance
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit

class MainViewModel(
    private val apiTazkRepository: ApiTazkRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var taskList : List<Task> = ArrayList()
    var selectedTask : Task? = null

    //newTask handlers
    var newTaskClickMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //getTasks handlers
    var getTasksMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var getTasksErrorMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //deleteTask handlers
    var deleteTaskMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //new/update task handlers
    var saveTaskClickMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var saveTaskMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    var selectedDate = Date()
    var dateSetMutableHandler = MutableLiveData<Boolean>()

    fun onNewTaskClick() {
        newTaskClickMutableHandler.postValue(true)
    }

    fun deleteTask(task: Task) {
        task.id?.let {
            uiScope.launch {
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.deleteTask(it)
                }
                Timber.d("DELETETASK REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
                if (response.isSuccessful) {
                    deleteTaskMutableHandler.postValue(true)
                } else {
                    Timber.d(response.errorBody().toString())
                    deleteTaskMutableHandler.postValue(false)
                }
            }
        }
    }

    fun onSaveTask() {
        Timber.d("TaskDialog - SAVE BUTTON PRESSED")
        saveTaskClickMutableHandler.postValue(true)
    }

    fun saveTask(task: Task) {
        uiScope.launch {
            val response = withContext(Dispatchers.IO) {
                selectedTask?.let { _ ->
                    apiTazkRepository.updateTask(task)
                } ?: run {
                    apiTazkRepository.createTask(task)
                }
            }
            Timber.d("GETTASKS REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    saveTaskMutableHandler.postValue(true)
                } ?: run {
                    Timber.d("Error al guardar tarea")
                }
            } else {
                Timber.d(response.errorBody().toString())
                Timber.d("Error al guardar tarea")
            }
        }
    }

    fun getTasks() {
        uiScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.format(selectedDate)
            val response = withContext(Dispatchers.IO) {
                apiTazkRepository.getTasksByDate(date, date)
            }
            Timber.d("GETTASKS REQUEST SUCCESS: ${response.isSuccessful} - ${response.body()}")
            if (response.isSuccessful) {
                response.body()?.let {
                    taskList = it.data
                    getTasksMutableHandler.postValue(true)
                } ?: run {
                    getTasksErrorMutableHandler.postValue(true)
                }
            } else {
                Timber.d(response.errorBody().toString())
                getTasksErrorMutableHandler.postValue(true)
            }
        }
    }

    fun goPreviousDate() {
        changeDay(-1)
        dateSetMutableHandler.value = true
    }

    fun goNextDate() {
        changeDay(1)
        dateSetMutableHandler.value = true
    }

    private fun changeDay(daysToAdd: Long) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val selectedInstant = selectedDate.toInstant()
            val goToInstant = selectedInstant.plus(daysToAdd, ChronoUnit.DAYS)
            selectedDate = Date.from(goToInstant)
        } else {
            selectedDate.time = selectedDate.time + (TimeUnit.DAYS.toMillis(daysToAdd))
        }
    }
}