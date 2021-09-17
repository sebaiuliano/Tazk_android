package com.tazk.tazk.util.listeners

interface CustomClickListener {
    fun onItemClick(item: Any, position: Int)
    fun onItemLongClick(item: Any, position: Int)
}