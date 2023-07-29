package com.example.cloudrent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.R
import com.example.cloudrent.response.Notification
import com.example.cloudrent.response.NotificationData
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.variabel.MenuVariabel
import kotlinx.coroutines.NonDisposableHandle.parent
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class NotifikasiAdapter(private val context: Context, private val notif: ArrayList<Notification>) :
    RecyclerView.Adapter<NotifikasiAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val pesan = view.findViewById<TextView>(R.id.pesan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.item_notifications, parent, false)
        return NotifikasiAdapter.MyViewHolder((itemView))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val nama_mobil = "ID#" + notif[position].data.pesanan.kode_pesanan
        val tanggal = parseDateBuat(notif[position].data.pesanan.created_at)
        val text = context.getString(R.string.notifikasi_konfirmasi, nama_mobil, tanggal)
        holder.pesan.text = text
    }

    override fun getItemCount(): Int = notif.size

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateBuat(dateString: String): String{
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())

        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    fun setData(data: List<Notification>) {
        notif.clear()
        notif.addAll(data)
        notifyDataSetChanged()
    }

}
