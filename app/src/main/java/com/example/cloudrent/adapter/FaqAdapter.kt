package com.example.cloudrent.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.R
import com.example.cloudrent.variabel.QuestVariabel

class FaqAdapter(private val context: Context, private val quest: List<QuestVariabel>, val listener: (QuestVariabel) -> Unit) :
    RecyclerView.Adapter<FaqAdapter.ListViewHolder>() {
    class ListViewHolder(view: View) :RecyclerView.ViewHolder(view)
    {
        val quest_text = view.findViewById<TextView>(R.id.quest)
        fun bindView( quest: QuestVariabel, listener: (QuestVariabel)
        -> Unit) {
            quest_text.text = quest.quest
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_faq,
                parent, false)
        )
    }
    override fun getItemCount(): Int = quest.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindView(quest[position], listener)
    }
}