package com.tazk.tazk.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.databinding.ItemTaskBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.util.listeners.CustomClickListener
import timber.log.Timber

class TasksAdapter(private val listener: CustomClickListener) : RecyclerView.Adapter<TaskViewHolder>() {

    private var taskList : MutableList<Task> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemTaskBinding.inflate(inflater, viewGroup,false)
        return TaskViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(taskList[position])
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun setTasks(list: List<Task>){
        taskList = list.toMutableList()
//        notifyItemRangeInserted(0, taskList.size)
        notifyDataSetChanged()
    }

    fun deleteTask(position: Int){
        taskList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItemFromList(position: Int) : Task? {
        return if (position <= taskList.size) {
            taskList[position]
        } else {
            Timber.d("Me caigo del array")
            null
        }

    }
}