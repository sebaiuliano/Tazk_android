package com.tazk.tazk.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.databinding.ItemTaskBinding
import com.tazk.tazk.entities.task.Task

class TasksAdapter : RecyclerView.Adapter<TaskViewHolder>() {

    private var taskList : MutableList<Task> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemTaskBinding.inflate(inflater, viewGroup,false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setTasks(list: List<Task>){
        taskList = list.toMutableList()
        notifyItemRangeInserted(0, taskList.size)
    }

    fun deleteTask(position: Int){
        taskList.removeAt(position)
        notifyItemRemoved(position)
    }
}