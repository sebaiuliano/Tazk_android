package com.tazk.tazk.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tazk.tazk.entities.network.request.DeleteImageRequest
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.repository.ApiTazkRepository
import com.tazk.tazk.repository.TaskRepository
import com.tazk.tazk.services.WifiService
import kotlinx.coroutines.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

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
    var file : File? = null
    var attachImageResponse : ImageResponse? = null
    var attachments: MutableList<ImageResponse> = ArrayList()
    var selectedAttachmentPosition : Int = -1
    var taskDate = selectedTask?.date ?: GregorianCalendar()
    var hasReminder = false
    var reminderDate = GregorianCalendar()

    //newTask handlers
    var newTaskClickMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //getTasks handlers
    var getTasksMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var getTasksErrorMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //sync tasks handler
    var syncTasksSuccessMutableHandler = MutableLiveData<Boolean>()
    var syncTasksFailureMutableHandler = MutableLiveData<Boolean>()

    //deleteTask handlers
    var deleteTaskMutableHandler: MutableLiveData<Boolean> = MutableLiveData()

    //new/update task handlers
    var saveTaskClickMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var saveTaskMutableHandler: MutableLiveData<Boolean> = MutableLiveData()
    var saveTaskErrorMutableHandler = MutableLiveData<Boolean>()
    var dateClickMutableHandler = MutableLiveData<Boolean>()
    var reminderCheckChangeMutableHandler = MutableLiveData<Boolean>()
    var reminderDateClickMutableHandler = MutableLiveData<Boolean>()
    var reminderTimeClickMutableHandler = MutableLiveData<Boolean>()
    var micClickMutableHandler = MutableLiveData<Boolean>()

    //filter handlers
    var filterApplyMutableHandler = MutableLiveData<Boolean>()

    //attach handlers
    var attachMutableHandler = MutableLiveData<Boolean>()
    var attachImageMutableHandler = MutableLiveData<Boolean>()
    var attachPhotoMutableHandler = MutableLiveData<Boolean>()
    var onAttachSuccessMutableHandler = MutableLiveData<Boolean>()
    var onAttachFailureMutableHandler = MutableLiveData<Boolean>()
    var onDeleteAttachmentSuccessMutableHandler = MutableLiveData<Boolean>()
    var onDeleteAttachmentFailureMutableHandler = MutableLiveData<Boolean>()

    //datepicker
    var selectedStartDate = Date()
    var selectedEndDate = Date()
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
                saveTaskErrorMutableHandler.postValue(true)
            }
        }
    }

    fun getTasks() {
        uiScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val startDate = sdf.format(selectedStartDate)
            val endDate = sdf.format(selectedEndDate)
            try {
                val response = withContext(Dispatchers.IO) {
                    apiTazkRepository.getTasksByDate(startDate, endDate, selectedCategoryFilter)
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
                Timber.e(e)
                getTasksErrorMutableHandler.postValue(true)
            }
        }
    }

    fun checkPendingTasks() {
        uiScope.launch {
            val tasks = withContext(Dispatchers.IO) {
                taskRepository.getAll()
            }
            if (WifiService.instance.isOnline()) {
                for (task in tasks) {
                    try {
                        val response = withContext(Dispatchers.IO) {
                            apiTazkRepository.createTask(task)
                        }
                        if (response) {
                            withContext(Dispatchers.IO) {
                                taskRepository.delete(task)
                            }
                            syncTasksSuccessMutableHandler.postValue(true)
                        } else {
                            syncTasksFailureMutableHandler.postValue(true)
                        }
                    } catch(e: Exception) {
                        Timber.e(e)
                        syncTasksFailureMutableHandler.postValue(true)
                    }
                }
            }
        }
    }

    fun onFilterApply() {
        filterApplyMutableHandler.postValue(true)
    }

    fun onAttach() {
        attachMutableHandler.postValue(true)
    }

    fun goGetImage() {
        attachImageMutableHandler.postValue(true)
    }

    fun goTakePhoto() {
        attachPhotoMutableHandler.postValue(true)
    }

    fun attachImage() {
        file?.let {
            uiScope.launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        apiTazkRepository.uploadImage(it)
                    }
                    println("UPLOAD IMAGE SUCCESS: ${response.isSuccessful} - ${response.body()}")
                    if (response.isSuccessful) {
                        attachImageResponse = response.body()?.data
                        onAttachSuccessMutableHandler.postValue(true)
                    } else {
                        attachImageResponse = null
                        onAttachFailureMutableHandler.postValue(true)
                    }
                } catch(e: Exception) {
                    Timber.e(e)
                    attachImageResponse = null
                    onAttachFailureMutableHandler.postValue(true)
                }
            }
        }
    }

    fun deleteAttachment(attachment: ImageResponse) {
        uiScope.launch {
            try {
                val responseAttachment = withContext(Dispatchers.IO) {
                    apiTazkRepository.deleteImage(DeleteImageRequest(attachment.publicId))
                }
                println("DELETE IMAGE SUCCESS: ${responseAttachment.isSuccessful} - ${responseAttachment.body()}")
                if (responseAttachment.isSuccessful) {
                    selectedTask?.let {
                        val responseTask = withContext(Dispatchers.IO) {
                            val auxList = it.image.toMutableList()
                            auxList.remove(attachment)
                            it.image = auxList
                            attachments = auxList
                            val response = apiTazkRepository.updateTask(it)
                            response
                        }
                        if (responseTask) {
                            onDeleteAttachmentSuccessMutableHandler.postValue(true)
                        } else {
                            onDeleteAttachmentFailureMutableHandler.postValue(true)
                        }
                    } ?: run {
                        attachments.remove(attachment)
                        onDeleteAttachmentSuccessMutableHandler.postValue(true)
                    }
                } else {
                    onDeleteAttachmentFailureMutableHandler.postValue(true)
                }
            } catch (e: Exception) {
                Timber.e(e)
                onDeleteAttachmentFailureMutableHandler.postValue(true)
            }
        }
    }

    fun onDateClick() {
        dateClickMutableHandler.postValue(true)
    }

    fun onReminderCheckChange() {
        reminderCheckChangeMutableHandler.postValue(true)
    }

    fun onReminderDateClick() {
        reminderDateClickMutableHandler.postValue(true)
    }

    fun onReminderTimeClick() {
        reminderTimeClickMutableHandler.postValue(true)
    }

    fun onMicClick() {
        micClickMutableHandler.postValue(true)
    }
}