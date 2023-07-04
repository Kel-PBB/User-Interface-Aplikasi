package com.example.cloudrent

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CardCardHomeAdapter(private val card_mobil: List<Mobil_Variabel>, val listener: (Mobil_Variabel) -> Unit): RecyclerView.Adapter<CardCardHomeAdapter.GridViewHolder>(){
        class GridViewHolder (view: View) :RecyclerView.ViewHolder(view){
            val fotomobil = view.findViewById<ImageView>(R.id.img_item_photo)
            val namamobil = view.findViewById<TextView>(R.id.nama_mobil)
            val hargamobil = view.findViewById<TextView>(R.id.harga_mobil)

            private val layoutManager = GridLayoutManager(view.context, 2)

            fun bindView(card_mobil: Mobil_Variabel, listener: (Mobil_Variabel) -> Unit){
                fotomobil.setImageResource((card_mobil.fotomobil))
                namamobil.text = card_mobil.namamobil
                hargamobil.text = card_mobil.hargamobil
            }
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType:
        Int): GridViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.card_mobil_home, parent, false)
            // create and set the GridLayoutManager with 2 columns
            val layoutManager = GridLayoutManager(parent.context, 2)
            return GridViewHolder(view)
        }
        override fun getItemCount(): Int = card_mobil.size
        override fun onBindViewHolder(holder: GridViewHolder, position:
        Int) {
            holder.bindView(card_mobil[position], listener)
        }
}