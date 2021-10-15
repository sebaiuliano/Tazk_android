package com.tazk.tazk.ui.main.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tazk.tazk.databinding.ItemAttachmentBinding
import com.tazk.tazk.entities.network.response.ImageResponse
import com.tazk.tazk.util.listeners.CustomClickListener

class AttachmentsAdapter(private val listener: CustomClickListener) : RecyclerView.Adapter<AttachmentViewHolder>() {

    private var attachmentList : MutableList<ImageResponse> = ArrayList()

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AttachmentViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        val binding = ItemAttachmentBinding.inflate(inflater, viewGroup,false)
        return AttachmentViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: AttachmentViewHolder, position: Int) {
        holder.bind(attachmentList[position])
    }

    override fun getItemCount(): Int {
        return attachmentList.size
    }

    fun setAttachments(list: List<ImageResponse>){
        attachmentList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun deleteAttachment(position: Int){
        attachmentList.removeAt(position)
        notifyItemRemoved(position)
    }
}