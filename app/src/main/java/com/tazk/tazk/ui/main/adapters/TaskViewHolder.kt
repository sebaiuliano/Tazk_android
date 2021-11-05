package com.tazk.tazk.ui.main.adapters

import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.databinding.ItemTaskBinding
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.util.Tools
import com.tazk.tazk.util.listeners.CustomClickListener

class TaskViewHolder(
    private val binding: ItemTaskBinding,
    private val listener: CustomClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) {
        setTask(task)
        itemView.setOnClickListener{ listener.onItemClick(task, adapterPosition) }
//        itemView.setOnLongClickListener {
//            listener.onItemLongClick(task, adapterPosition)
//            true
//        }
    }

    private fun setTask(task: Task){
        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
        binding.tvTaskDate.text = Tools.gregorianCalendarToString(task.date, "dd-MM-yyyy")
    }
}