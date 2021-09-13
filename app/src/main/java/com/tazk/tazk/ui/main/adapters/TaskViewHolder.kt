package com.tazk.tazk.ui.main.adapters

import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.databinding.ItemTaskBinding
import com.tazk.tazk.entities.task.Task

class TaskViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(task: Task) {
        setTask(task)
//        itemView.setOnClickListener{ listener.onItemClick(task, adapterPosition) }
    }

    private fun setTask(task: Task){
        binding.tvTaskTitle.text = task.title
        binding.tvTaskDescription.text = task.description
    }
}