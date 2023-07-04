package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.*
import com.example.cloudrent.variabel.SpesifikasiVariabel
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class DetailMobilActivity : AppCompatActivity() {
    private  lateinit var progressBar: LinearLayout
    private lateinit var harga: TextView
    private lateinit var totalHari: TextView
    private lateinit var totalHarga: TextView
    private lateinit var manual: TextView
    private lateinit var seat: TextView
    private lateinit var mesin: TextView
    private lateinit var brand: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var gambar: ImageView
    private lateinit var btnPesan: Button

    private lateinit var recyclerView: RecyclerView
    val DatamSpek = listOf<SpesifikasiVariabel>(
        SpesifikasiVariabel(
            R.drawable.img_mesin,
            judul_spek = "Bahan Bakar",
            isi_spek = "Bensin"
        ),
        SpesifikasiVariabel(
            R.drawable.img_mesin,
            judul_spek = "Mesin",
            isi_spek = "Mesin"
        ),
        SpesifikasiVariabel(
            R.drawable.img_seater,
            judul_spek = "Seater",
            isi_spek = "Seater"
        )
    )
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mobil)

        progressBar = findViewById(R.id.progressBar_detail)
        harga = findViewById(R.id.harga_hari)
        manual = findViewById(R.id.transmisi)
        seat = findViewById(R.id.seat)
        mesin = findViewById(R.id.mesin)
        brand = findViewById(R.id.brand)
        totalHari = findViewById(R.id.total_hari)
        totalHarga = findViewById(R.id.harga_total)
        toolbar = findViewById(R.id.toolbar_detail)
        gambar = findViewById(R.id.mobil_gam)
        val btnPesan: Button = findViewById(R.id.btnPesan)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }
        val sharedPreferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AuthToken", "") ?: ""
        val kode_mobil = intent.getStringExtra("kode_mobil").toString()
        val mobil_id = intent.getStringExtra("mobil_id").toString()

        detailMobil(token, kode_mobil)

        btnPesan.setOnClickListener {
            val intent = Intent(this, FormPesananActivity::class.java).apply {
                putExtra("nama_mobil", toolbar.title.toString())
                putExtra("token", token)
                putExtra("mobil_id", mobil_id)
            }
            startActivity(intent)
        }

    }

    private fun detailMobil(token: String, kode_mobil: String) {
        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.detailMobil(kode_mobil).enqueue(object : Callback<MobilDetail> {
                override fun onResponse(call: Call<MobilDetail>, response: Response<MobilDetail>) {
                    if (response.isSuccessful) {
                        val responseDataList = response.body()
                        val mobil = responseDataList?.mobil
                        val search = responseDataList?.search
                        val total = responseDataList?.total
                        val days = responseDataList?.days
                        updateUI(mobil, search, total, days)
                        progressBar.visibility = View.GONE
                        // Process the successful response
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@DetailMobilActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MobilDetail>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("SearchMobil", t.stackTraceToString())
                    Toast.makeText(this@DetailMobilActivity, "An error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun updateUI(mobil: MobilClass?, search: Search?, total: Int?, days: Int?){
        toolbar.setTitle(mobil?.nama)
        harga.setText("IDR "+ mobil?.harga)
        val hargaTot = formatNumber(total)
        val gambar_rl = "https://cloudrental.my.id/storage/" + mobil?.gambar
        Picasso.get()
            .load(gambar_rl)
            .into(gambar)
        totalHarga.setText("IDR " + hargaTot)
        totalHari.setText(days.toString() + " hari")
        brand.setText(mobil?.brand)
        manual.setText(mobil?.transmisi)
        seat.setText(mobil?.seat + " seat")
        mesin.setText(mobil?.mesin + " CC")
    }

    private fun formatNumber(number: Int?): String{
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
//    private fun ShowSpekMahasiswa() {
//        val adapter = SpesifikasiAdapter(card_spek = DatamSpek){
//        }
//        val recyclerView =
//            findViewById<RecyclerView>(R.id.rv_spek)
//        recyclerView.layoutManager = GridLayoutManager (this, 2)
//        recyclerView.adapter = SpesifikasiAdapter (DatamSpek) {
//        }
//    }
}