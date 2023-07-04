package com.example.cloudrent.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.DetailMobilActivity
import com.example.cloudrent.R
import com.example.cloudrent.variabel.DetailVariabel
import java.util.EventListener

class Card_MahasiswaAdapter(private val context: Context, private val dataList: List<DetailVariabel>, val listener: (DetailVariabel) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType:
    Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_detail_mobil, parent, false)
        return MyViewHolder(view)
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as MyViewHolder
        myHolder.bindView(dataList[position], listener)
    }
    override fun getItemCount(): Int {
        return dataList.size
    }
    inner class MyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        val cardView = itemView.findViewById<CardView>(R.id.card_mobil)
        val foto_mobil= itemView.findViewById<ImageView>(R.id.gambar_mobil)
        val nama_mobil = itemView.findViewById<TextView>(R.id.nama_mobil)
        fun bindView(mobil: DetailVariabel, listener: (DetailVariabel) -> Unit) {
            cardView.setOnClickListener { listener(mobil)
                val intent = Intent(context, DetailMobilActivity::class.java)
                intent.putExtra("nim_mhs", mobil.nama_mobil)
                context.startActivity(intent)
            }
            foto_mobil.setImageResource(mobil.fotomobil)
            nama_mobil.setText(mobil.nama_mobil)
        }
    }
}
