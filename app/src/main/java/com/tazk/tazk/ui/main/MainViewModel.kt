package com.tazk.tazk.ui.main

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
import java.time.LocalDateTime
import java.util.*

class MainViewModel(
    private val apiTazkRepository: ApiTazkRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var taskList : List<Task> = ArrayList()
    var selectedTask : Task? = null
    lateinit var account : GoogleSignInAccount

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

    fun onNewTaskClick() {
        println("CLICK NEW TASK")
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
            account.id?.let {
                val taskRequest = task.toTaskRequest()
                taskRequest.email = account.email ?: ""
                val response = withContext(Dispatchers.IO) {
                    selectedTask?.let { _ ->
                        apiTazkRepository.updateTask(taskRequest)
                    } ?: run {
                        apiTazkRepository.createTask(taskRequest)
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
    }

    fun getTasks() {
        uiScope.launch {
            account.id?.let {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val date = sdf.format(Date())
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.getTasksByDate(account.email, date, date)
//                    apiTazkRepository.getTasksByDate("nachgrandi@gmail.com", "2021-01-02", "2021-01-02")
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
    }
}