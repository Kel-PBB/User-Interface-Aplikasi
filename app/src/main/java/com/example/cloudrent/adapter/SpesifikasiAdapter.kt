package com.example.cloudrent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.Mobil_Variabel
import com.example.cloudrent.R
import com.example.cloudrent.variabel.SpesifikasiVariabel

class SpesifikasiAdapter(private val card_spek: List<SpesifikasiVariabel>, val listener: (SpesifikasiVariabel) -> Unit): RecyclerView.Adapter<SpesifikasiAdapter.GridViewHolder>(){
    class GridViewHolder (view: View) :RecyclerView.ViewHolder(view){
        val fotospek = view.findViewById<ImageView>(R.id.img_spek)
        val judul_spek = view.findViewById<TextView>(R.id.judul_spek)
        val isi_spek = view.findViewById<TextView>(R.id.isi_spek)

        private val layoutManager = GridLayoutManager(view.context, 2)

        fun bindView(card_spek: SpesifikasiVariabel, listener: (SpesifikasiVariabel) -> Unit){
            fotospek.setImageResource((card_spek.fotospek))
            judul_spek.text = card_spek.judul_spek
            isi_spek.text = card_spek.isi_spek
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType:
    Int): GridViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.spesifikasi_mobil, parent, false)
        // create and set the GridLayoutManager with 2 columns
        val layoutManager = GridLayoutManager(parent.context, 2)
        return GridViewHolder(view)
    }
    override fun getItemCount(): Int = card_spek.size
    override fun onBindViewHolder(holder: GridViewHolder, position:
    Int) {
        holder.bindView(card_spek[position], listener)
    }
}