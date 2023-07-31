package com.example.cloudrent.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.pesananactivity.PesananDetailActivity
import com.example.cloudrent.pesananactivity.PesananJemputDetailActivity
import com.example.cloudrent.R
import com.example.cloudrent.response.Pesanans
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class PesananListAdapter (private  val context: Context, private val dataList: ArrayList<Pesanans>):
    RecyclerView.Adapter<PesananListAdapter.MyViewHolder>(){
    class MyViewHolder(val view: View): RecyclerView.ViewHolder(view){
        val tvNama = view.findViewById<TextView>(R.id.namaMobil)
        val tvHari = view.findViewById<TextView>(R.id.hari)
        val tvTotal = view.findViewById<TextView>(R.id.total)
        val tvTglM = view.findViewById<TextView>(R.id.tglMualai)
        val tvTglS = view.findViewById<TextView>(R.id.tglSelesai)
        val tvJam = view.findViewById<TextView>(R.id.jam)
        val tvTglPesan = view.findViewById<TextView>(R.id.tglPesan)
        val tvStatus = view.findViewById<TextView>(R.id.status)
        val cardPesanan = view.findViewById<CardView>(R.id.card_pesanan_list)
        val cardMenu = view.findViewById<CardView>(R.id.card_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.card_pesanan, parent, false)
        return MyViewHolder((itemView))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val tgl_m = parseDate(dataList[position].tanggal_rental_mulai)
        val tgl_s = parseDate(dataList[position].tanggal_rental_selesai)
        val jam = parseTimeString(dataList[position].waktu_pengambilan)
        val tot = formatNumber(dataList[position].total)

        val days = countDay(dataList[position].tanggal_rental_mulai, dataList[position].tanggal_rental_selesai)
        val tglP = parseDateBuat(dataList[position].created_at)

        holder.tvNama.text = dataList[position].mobil.nama
        holder.tvStatus.text = dataList[position].status.name
        holder.tvTglM.text = tgl_m
        holder.tvTglS.text = tgl_s
        holder.tvTotal.text = tot
        holder.tvJam.text = jam
        holder.tvHari.text = days
        holder.tvTglPesan.text = tglP

        holder.cardPesanan.setOnClickListener{
            if(dataList[position].status.id.equals(2)){
                val intent = Intent(context, PesananDetailActivity::class.java).apply {
                    putExtra("kode_pesanan", dataList[position].kode_pesanan)
                }
                context.startActivity(intent)
            }else if(dataList[position].status.id.equals(3)){
                val intent = Intent(context, PesananJemputDetailActivity::class.java).apply {
                    putExtra("kode_pesanan", dataList[position].kode_pesanan)
                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = dataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Pesanans>) {
        dataList.clear()
        dataList.addAll(data)
        notifyDataSetChanged()
    }

    fun checkDatalist(): StatusInfo {
        val first = dataList.firstOrNull()?.status_id
        val allstatus = dataList.all { it.status_id == first }
        val statusIds = dataList.map { it.status_id }.distinct()
        Toast.makeText(context, first, Toast.LENGTH_SHORT).show()
        if(allstatus){
            return (StatusInfo(allstatus, first))
        }else{
            return StatusInfo(allstatus, null)
        }
    }

    data class StatusInfo(val status: Boolean, val first: String?)

    fun clearData(){
        dataList.clear()
    }

    private  fun parseDate(dateString: String): String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault())

        val date: Date = inputFormat.parse(dateString) ?: return ""
        return outputFormat.format(date)
    }

    fun parseTimeString(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val time: Date = inputFormat.parse(timeString) ?: return ""
        val parsedTime = outputFormat.format(time)

        return "$parsedTime"
    }

    private fun formatNumber(number: String?): String {
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return try {
            val numericValue = number?.toDouble()
            if (numericValue != null) {
                numberFormat.format(numericValue)
            } else {
                ""
            }
        } catch (e: NumberFormatException) {
            ""
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateBuat(dateString: String): String{
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())

        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun countDay(tglM: String?, tglS: String?): String {
        val tgl_M = LocalDate.parse(tglM)
        val tgl_S = LocalDate.parse(tglS)
        val days = ChronoUnit.DAYS.between(tgl_M, tgl_S)

        return "$days"
    }
}