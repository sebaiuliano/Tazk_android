package com.tazk.tazk.ui.main

import android.graphics.Canvas
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.R
import com.tazk.tazk.databinding.ActivityMainBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.ui.main.adapters.TasksAdapter
import com.tazk.tazk.util.SwipeToDeleteCallback
import com.tazk.tazk.util.listeners.CustomClickListener
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), CustomClickListener {
    private val model: MainViewModel by viewModel()
    private lateinit var binding : ActivityMainBinding
    private val tasksAdapter = TasksAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setObservers()
        initializeTasksRecyclerView()
    }

    private fun setObservers() {

    }

    private fun initializeTasksRecyclerView() {
//        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//                model.deleteTask()
//                Toast.makeText(this@MainActivity, "Tarea eliminada", Toast.LENGTH_SHORT).show()
//                tasksAdapter.deleteTask(viewHolder.adapterPosition)
//            }
//
//        }

        binding.rvTasks.setHasFixedSize(true)
//        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(this, tasksAdapter))
        itemTouchHelper.attachToRecyclerView(binding.rvTasks)
        binding.rvTasks.adapter = tasksAdapter
        tasksAdapter.setTasks(listOf(
            Task("Titulo1", "Descripcion1"),
            Task("Titulo2", "Descripcion2")
        ))
    }

    override fun onItemClick(item: Any, position: Int) {
        //TODO hacer algo?
    }


}