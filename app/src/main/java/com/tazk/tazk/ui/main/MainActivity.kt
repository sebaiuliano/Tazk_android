package com.tazk.tazk.ui.main

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityMainBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.ui.main.adapters.TasksAdapter
import com.tazk.tazk.ui.main.dialogs.TaskDialogFragment
import com.tazk.tazk.util.SwipeToDeleteCallback
import com.tazk.tazk.util.listeners.CustomClickListener
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber

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
        getTasks()
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
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                getTasks()
            }
        }

        model.deleteTaskMutableHandler.observe(this) {
            if (it) {
                deleteTaskSuccess()
            } else {
                deleteTaskFailure()
            }
            getTasks()
        }

        model.saveTaskMutableHandler.observe(this) {
            if (it) {
                model.saveTaskMutableHandler.value = false
                getTasks()
                model.selectedTask = null
            }
        }
    }

    private fun initializeTasksRecyclerView() {
        binding.rvTasks.setHasFixedSize(true)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, tasksAdapter, model))
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
        binding.rvTasks.adapter = tasksAdapter
    }

    private fun getTasks() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        account?.let {
            model.account = it
            model.getTasks()
        } ?: run {
            Timber.d("NO SE ENCONTRO CUENTA ASOCIADA, VUELVO A LOGINACTIVITY")
            onBackPressed()
        }
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

}