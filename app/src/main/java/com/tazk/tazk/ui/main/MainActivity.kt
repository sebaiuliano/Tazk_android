package com.tazk.tazk.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.messaging.FirebaseMessaging
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityMainBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.ui.main.adapters.TasksAdapter
import com.tazk.tazk.ui.main.dialogs.FilterDialogFragment
import com.tazk.tazk.ui.main.dialogs.TaskDialogFragment
import com.tazk.tazk.util.SwipeToDeleteCallback
import com.tazk.tazk.util.Tools
import com.tazk.tazk.util.listeners.CustomClickListener
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*

class MainActivity : AppCompatActivity(), CustomClickListener {

    private val model: MainViewModel by viewModel()
    private lateinit var binding : ActivityMainBinding
    private val tasksAdapter = TasksAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainViewModel = model
        binding.lifecycleOwner = this

        setObservers()
        initializeTasksRecyclerView()
        model.selectedStartDate.time = System.currentTimeMillis()
        model.selectedEndDate.time = System.currentTimeMillis()
        //setDate()
        model.categoriesList = resources.getStringArray(R.array.list_categories).toList()
        model.getTasks()
    }

    override fun onResume() {
        super.onResume()
        model.checkPendingTasks()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_default, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.filter -> { openFilterDialog() }
            R.id.history -> { openDatePicker() }
        }
        return true
    }

    private fun setObservers() {
        model.newTaskClickMutableHandler.observe(this) {
            if (it) {
                model.newTaskClickMutableHandler.value = false
                goTaskFragment(null)
            }
        }

        model.getTasksMutableHandler.observe(this) {
            if (it) {
                model.getTasksMutableHandler.value = false
                tasksAdapter.setTasks(model.taskList)
            }
        }

        model.getTasksErrorMutableHandler.observe(this) {
            if (it) {
                model.getTasksErrorMutableHandler.value = false
                Toast.makeText(this, "No se pudieron obtener las tareas", Toast.LENGTH_SHORT).show()
            }
        }

        model.deleteTaskMutableHandler.observe(this) {
            if (it) {
                deleteTaskSuccess()
            } else {
                deleteTaskFailure()
            }
            model.getTasks()
        }

        model.saveTaskMutableHandler.observe(this) {
            if (it) {
                model.saveTaskMutableHandler.value = false
                model.getTasks()
                model.selectedTask = null
            }
        }

        model.dateSetMutableHandler.observe(this){
            if (it) {
                model.dateSetMutableHandler.value = false
                //setDate()
                model.getTasks()
            }
        }

        model.saveTaskErrorMutableHandler.observe(this){
            if (it) {
                model.saveTaskErrorMutableHandler.value = false
                saveTaskFailure()
            }
        }
    }

    private fun initializeTasksRecyclerView() {
        binding.rvTasks.setHasFixedSize(true)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, tasksAdapter, model))
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
        binding.rvTasks.adapter = tasksAdapter
    }

    private fun goTaskFragment(task: Task?) {
        model.selectedTask = task
        val fragment = TaskDialogFragment()
        fragment.show(supportFragmentManager, "task")
    }

    override fun onItemClick(item: Any, position: Int) {
        if (item is Task) {
            goTaskFragment(item)
        }
    }

    override fun onItemLongClick(item: Any, position: Int) {
        if (item is Task) {
            goTaskFragment(item)
        }
    }

    private fun deleteTaskSuccess() {
        Toast.makeText(this, "Tarea eliminada correctamente", Toast.LENGTH_SHORT).show()
    }

    private fun deleteTaskFailure() {
        Toast.makeText(this, "Ocurrió un error al eliminar la tarea", Toast.LENGTH_SHORT).show()
    }

    private fun saveTaskFailure() {
        Toast.makeText(this, "Ocurrió un error al guardar la tarea", Toast.LENGTH_SHORT).show()
    }

    private fun openDatePicker() {
        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setTitleText(resources.getString(R.string.select_date))
            .setSelection(androidx.core.util.Pair(Calendar.getInstance().timeInMillis, Calendar.getInstance().timeInMillis))
            .build()

        datePicker.addOnPositiveButtonClickListener {
            var dateStart = Date()
            dateStart.time = it.first
            dateStart = Tools.dateWithOffset(dateStart)
            var dateEnd = Date()
            dateEnd.time = it.second
            dateEnd = Tools.dateWithOffset(dateEnd)
            model.selectedStartDate = dateStart
            model.selectedEndDate = dateEnd
            model.dateSetMutableHandler.value = true
            datePicker.dismiss()
        }

        datePicker.addOnNegativeButtonClickListener {
            var dateStart = Date()
            dateStart.time = System.currentTimeMillis()
            dateStart = Tools.dateWithOffset(dateStart)
            var dateEnd = Date()
            dateEnd.time = System.currentTimeMillis()
            dateEnd = Tools.dateWithOffset(dateEnd)
            model.selectedStartDate = dateStart
            model.selectedEndDate = dateEnd
            model.dateSetMutableHandler.value = true
            datePicker.dismiss()
        }

        datePicker.show(supportFragmentManager, "date_picker")
    }

    private fun openFilterDialog() {
        val fragment = FilterDialogFragment()
        fragment.show(supportFragmentManager, "filter")
    }
}