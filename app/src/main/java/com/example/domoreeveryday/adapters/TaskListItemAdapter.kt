package com.example.domoreeveryday.adapters

import android.content.Context
import android.content.res.Resources
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.domoreeveryday.model.Task

class TaskListItemAdapter (private val context: Context, private var list: ArrayList<Task>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
return list.size
    }

    private fun Int.toDp(): Int = (this/ Resources.getSystem().displayMetrics.density).toInt()


    private fun Int.toDx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}
