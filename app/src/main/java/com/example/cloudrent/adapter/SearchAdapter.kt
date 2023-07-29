package com.example.cloudrent.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.DetailMobilActivity
import com.example.cloudrent.R
import com.example.cloudrent.SearchActivity
import com.example.cloudrent.response.Mobil
import com.squareup.picasso.Picasso

class SearchAdapter (private  val context: Context, private val dataList: ArrayList<Mobil>):RecyclerView.Adapter<SearchAdapter.MyViewHolder>(){


    class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val tvNama = view.findViewById<TextView>(R.id.nama_mobil)
        val tvBrand = view.findViewById<TextView>(R.id.brand)
        val tvHarga = view.findViewById<TextView>(R.id.harga_mobil)
        val tvMesin = view.findViewById<TextView>(R.id.mesin)
        val tvTransmisi = view.findViewById<TextView>(R.id.transmisi)
        val tvSeat = view.findViewById<TextView>(R.id.seat)
        val tvMobilImage = view.findViewById<ImageView>(R.id.img_item_photo)
        val cardMobil = view.findViewById<CardView>(R.id.card_mobil)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.card_mobil_home, parent, false)
        return MyViewHolder((itemView))
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvNama.text = dataList[position].nama
        holder.tvBrand.text = dataList[position].brand
        holder.tvHarga.text = dataList[position].harga
        holder.tvSeat.text = dataList[position].seat + " seat"
        holder.tvMesin.text = dataList[position].mesin + " CC"
        holder.tvTransmisi.text = dataList[position].transmisi
        val gambar_rl = "https://cloudrental.my.id/storage/" + dataList[position].gambar
        Picasso.get()
            .load(gambar_rl)
            .into(holder.tvMobilImage)

        holder.cardMobil.setOnClickListener{
            val intent = Intent(context, DetailMobilActivity::class.java).apply {
                putExtra("kode_mobil", dataList[position].kode_mobil)
                putExtra("mobil_id", dataList[position].id.toString())
            }
            context.startActivity(intent)
//            Toast.makeText(context, dataList[position].id.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = dataList.size

    fun setData(data: List<Mobil>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }
}