package com.example.cloudrent

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Invoice
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.response.ResponsePesananShow
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

class PesananJemputDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var namaMobil: TextView
    private lateinit var tglM: TextView
    private lateinit var tglS: TextView
    private lateinit var tglPesan: TextView
    private lateinit var totalH: TextView
    private lateinit var jam: TextView
    private lateinit var idInv: TextView
    private lateinit var bank: TextView
    private lateinit var tot: TextView
    private lateinit var norek: TextView
    private lateinit var token: String
    private lateinit var kode_pesanan: String
    private lateinit var progress: LinearLayout
    private lateinit var errorMessage: LinearLayout
    private lateinit var statusInvoice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan_jemput_detail)

        kode_pesanan = intent.getStringExtra("kode_pesanan").toString()
        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("AuthToken", "") ?: ""

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_form_pesanan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.setNavigationIcon(R.drawable.baseline_clear_24)
        toolbar.title = ""
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }

        namaMobil = findViewById(R.id.nama_mobil)
        tglM = findViewById(R.id.pesan_tgl_mulai)
        tglS = findViewById(R.id.pesan_tgl_selesai)
        tglPesan = findViewById(R.id.tgl_pesan)
        totalH = findViewById(R.id.total_hari)
        jam = findViewById(R.id.jam)
        idInv = findViewById(R.id.id_invoice)
        bank = findViewById(R.id.nama_bank)
        tot = findViewById(R.id.total_transfer)
        norek = findViewById(R.id.norek)
        progress = findViewById(R.id.progressBar)
        errorMessage = findViewById(R.id.error_text)
        statusInvoice = findViewById(R.id.status_invoice)

        pesananShow(token, kode_pesanan)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true

        val sydney = LatLng(-33.8852, 151.211)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12f))
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private fun pesananShow(token: String, kode_pesanan: String)
    {
        progress.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        val apiService = ApiClient.create(token)
        apiService.pesananShow(kode_pesanan).enqueue(object : Callback<ResponsePesananShow> {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(
                call: Call<ResponsePesananShow>,
                response: Response<ResponsePesananShow>
            ) {
                if(response.isSuccessful){
                    val responseData = response.body()
                    val invoice = responseData?.invoice
                    val pesanan = responseData?.pesanan
//                    Toast.makeText(this@PesananDetailActivity, pesanan.toString(), Toast.LENGTH_LONG).show()
                    updateUi(invoice, pesanan)
                    progress.visibility = View.GONE
                }else{
                    Toast.makeText(this@PesananJemputDetailActivity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponsePesananShow>, t: Throwable) {
                errorMessage.visibility = View.VISIBLE
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateUi(invoice: Invoice?, pesanan: Pesanans?)
    {
        val tglMulai = parseDate(pesanan?.tanggal_rental_mulai.toString())
        val tglSelesai = parseDate(pesanan?.tanggal_rental_selesai)
        val jamM = parseTimeString(pesanan?.waktu_pengambilan.toString())
        val tglPes = parseDateBuat(pesanan?.created_at.toString())
        val days = getHari(pesanan?.tanggal_rental_mulai.toString(), pesanan?.tanggal_rental_selesai.toString())
        val totP = formatNumber(invoice?.total?.toInt())
        val status_invoice = invoice?.status?.id

        if (status_invoice?.equals(1)!!){
            statusInvoice.setText("Lunas")
        }else{
            statusInvoice.setText("Belum Lunas")
        }

        tglM.setText(tglMulai)
        tglS.setText(tglSelesai)
        namaMobil.setText(pesanan?.mobil?.nama)
        tglPesan.setText(tglPes)
        jam.setText(jamM)
        totalH.setText(days)
        idInv.setText(invoice?.no_invoice)
        bank.setText(invoice?.nama_bank)
        norek.setText(invoice?.norek)
        tot.setText(totP)

    }

    fun parseTimeString(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val time: Date = inputFormat.parse(timeString) ?: return ""
        val parsedTime = outputFormat.format(time)

        return "$parsedTime"
    }

    private  fun parseDate(dateString: String?): String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale("id", "ID"))
        val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

        val date: Date = inputFormat.parse(dateString) ?: return ""
        return outputFormat.format(date)
    }

    fun parseTimeStringTo(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val time: Date = inputFormat.parse(timeString) ?: return ""
        val parsedTime = outputFormat.format(time)

        return "$parsedTime"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getHari(tglM: String, tglS: String): String {
        val tglMul = LocalDate.parse(tglM)
        val tglSel = LocalDate.parse(tglS)
        val day = ChronoUnit.DAYS.between(tglMul, tglSel)
        return "$day"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDateBuat(dateString: String): String{
        val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.getDefault())

        val dateTime = LocalDateTime.parse(dateString, inputFormatter)
        return dateTime.format(outputFormatter)
    }

    private fun formatNumber(number: Int?): String{
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }
}