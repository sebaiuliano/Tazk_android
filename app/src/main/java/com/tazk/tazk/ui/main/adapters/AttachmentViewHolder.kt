package com.tazk.tazk.ui.main.adapters

import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tazk.tazk.databinding.ItemAttachmentBinding
import com.tazk.tazk.databinding.ItemTaskBinding
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.entities.task.Task
import com.tazk.tazk.util.listeners.CustomClickListener

class AttachmentViewHolder (
    private val binding: ItemAttachmentBinding,
    private val listener: CustomClickListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageResponse: ImageResponse) {
        setAttachment(imageResponse)
        itemView.setOnClickListener {
            listener.onItemClick(imageResponse, adapterPosition)
        }
    }

    private fun setAttachment(attachment: ImageResponse){
        Picasso.get().load(attachment.url).into(binding.img)
    }
}