package com.tazk.tazk.ui.main

import android.os.Build
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.common.api.ApiException
import com.tazk.tazk.R
import com.tazk.tazk.entities.network.response.TasksResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.repository.ApiTazkRepository
import com.tazk.tazk.repository.TaskRepository
import com.tazk.tazk.util.services.WifiService
import kotlinx.coroutines.*
import retrofit2.Response
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

class MainViewModel(
    private val apiTazkRepository: ApiTazkRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var taskList : List<Task> = ArrayList()
    var selectedTask : Task? = null
    var selectedCategory = ""
    var selectedCategoryFilter : String? = null
    var categoriesList : List<String> = ArrayList()

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
    var saveTaskErrorMutableHandler = MutableLiveData<Boolean>()

    //filter handlers
    var filterApplyMutableHandler = MutableLiveData<Boolean>()

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
                if (response) {
                    deleteTaskMutableHandler.postValue(true)
                } else {
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
            val success = withContext(Dispatchers.IO) {
                selectedTask?.let { _ ->
                    apiTazkRepository.updateTask(task)
                } ?: run {
                    apiTazkRepository.createTask(task)
                }
            }
            if (success) {
                saveTaskMutableHandler.postValue(true)
            } else {

            }
        }
    }

    fun getTasks() {
        uiScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.format(selectedDate)
            try {
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.getTasksByDate(date, date, selectedCategoryFilter)
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
            } catch(e: IOException) {
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

    fun checkPendingTasks() {
        uiScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskRepository.getAll()
            }
            if (WifiService.instance.isOnline()) {
                for (task in tasks) {
                    val response = withContext(Dispatchers.IO) {
                        apiTazkRepository.createTask(task)
                    }
                    if (response) {
                        withContext(Dispatchers.IO) {
                            taskRepository.delete(task)
                        }
                    }
                }
            }
        }
    }

    fun onFilterApply() {
        filterApplyMutableHandler.postValue(true)
    }
}