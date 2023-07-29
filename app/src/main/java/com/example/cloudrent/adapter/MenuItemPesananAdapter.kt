package com.example.cloudrent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.R
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.variabel.MenuVariabel

class MenuItemPesananAdapter(private val context: Context, private val menu: List<MenuVariabel>, private val pesanan: ArrayList<Pesanans>, private val pesananListAdapter: PesananListAdapter,val listener: (MenuVariabel) -> Unit) :
    RecyclerView.Adapter<MenuItemPesananAdapter.MyViewHolder>() {
    private var selectedPosition = -1
    class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val card_menu = view.findViewById<CardView>(R.id.card_menu)
        val menu_judul = view.findViewById<TextView>(R.id.judul_menu)
        val total = view.findViewById<TextView>(R.id.total_pesanan_per)
        val frameTot = view.findViewById<LinearLayout>(R.id.frame_total)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_menu_pesanan, parent, false)
        return MenuItemPesananAdapter.MyViewHolder((itemView))
    }
    override fun getItemCount(): Int = menu.size

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentMenu = menu[position]
        holder.menu_judul.setText(menu[position].menu)
        if(menu[position].jumlah.equals(0)){
            holder.frameTot.visibility = View.GONE
        }else{
            holder.total.setText(menu[position].jumlah.toString())
        }

        val backgroundColor = if (currentMenu.isSelected) {
            ContextCompat.getColor(context, R.color.white)
        } else {
            ContextCompat.getColor(context, R.color.birtud_trans)
        }
        holder.card_menu.setCardBackgroundColor(backgroundColor)

        holder.card_menu.setOnClickListener{
            currentMenu.isSelected = !currentMenu.isSelected
            updateSelectedMenu(position)
            listener.invoke(currentMenu)
        }

    }

    fun updateSelectedMenu(selectedPosition: Int) {
        for (i in menu.indices) {
            val menu = menu[i]
            menu.isSelected = (i == selectedPosition)
        }
        notifyDataSetChanged()
    }


    private fun setDataToAdapter(data: kotlin.collections.List<Pesanans>) {
        pesananListAdapter.setData(data)
    }
}