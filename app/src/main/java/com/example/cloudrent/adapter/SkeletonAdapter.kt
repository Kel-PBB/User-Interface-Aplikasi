package com.example.cloudrent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.R

class SkeletonAdapter(private val itemCount: Int) : RecyclerView.Adapter<SkeletonAdapter.SkeletonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkeletonViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.skeleton_card_mobil_home, parent, false)
        return SkeletonViewHolder(view)
    }

    override fun onBindViewHolder(holder: SkeletonViewHolder, position: Int) {
        // No data binding required for skeleton view
    }

    override fun getItemCount(): Int {
        return itemCount
    }

    class SkeletonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}