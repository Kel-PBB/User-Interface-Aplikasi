package com.example.cloudrent

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewStub
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.*
import com.example.cloudrent.variabel.SpesifikasiVariabel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.MaterialToolbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class DetailMobilActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private lateinit var harga: String
    private lateinit var totalHari: String
    private lateinit var totalHarga: TextView
    private lateinit var totalHargaR: String
    private lateinit var manual: TextView
    private lateinit var seat: TextView
    private lateinit var mesin: TextView
    private lateinit var brand: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var gambar: ImageView
    private lateinit var btnRincian: ImageView
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_mobil)

        mapView = findViewById(R.id.mapViewCar)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        manual = findViewById(R.id.transmisi)
        seat = findViewById(R.id.seat)
        mesin = findViewById(R.id.mesin)
        brand = findViewById(R.id.brand)

        totalHarga = findViewById(R.id.harga_total_a)
        toolbar = findViewById(R.id.toolbar_detail)
        gambar = findViewById(R.id.mobil_gam)

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        applyShimmerToAllShimmerFrames(rootView)
        btnRincian = findViewById(R.id.show_rincian)
        val btnPesan: Button = findViewById(R.id.btnPesan)
        val s_pesan: TextView = findViewById(R.id.arra_sebelum_pesan)
        val s_pesan_array = resources.getStringArray(R.array.sebelum_pesan)
        val s_pesan_text = s_pesan_array.joinToString("\n")
        s_pesan.text = s_pesan_text

        val se_pesan: TextView = findViewById(R.id.array_setelah_pesan)
        val se_pesan_array = resources.getStringArray(R.array.setelah_anda_pesan)
        val se_pesan_text = se_pesan_array.joinToString("\n")
        se_pesan.text = se_pesan_text

        val se_pengam: TextView = findViewById(R.id.arra_saat_pengambilan)
        val se_pengam_array = resources.getStringArray(R.array.saat_pengambilan)
        val se_pengam_text = se_pengam_array.joinToString("\n")
        se_pengam.text = se_pengam_text

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_detail)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_arrow_left_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }
        val sharedPreferences = this.getSharedPreferences("Login", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AuthToken", "") ?: ""
        val kode_mobil = intent.getStringExtra("kode_mobil").toString()
        val mobil_id = intent.getStringExtra("mobil_id").toString()

        detailMobil(token, kode_mobil)

        btnRincian.setOnClickListener{
            showDialog()
        }


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
        val apiService = ApiClient.create(token)
        apiService.detailMobil(kode_mobil).enqueue(object : Callback<MobilDetail> {
                override fun onResponse(call: Call<MobilDetail>, response: Response<MobilDetail>) {
                    if (response.isSuccessful) {
                        val responseDataList = response.body()
                        val mobil = responseDataList?.mobil
                        val search = responseDataList?.search
                        val total = responseDataList?.total
                        val days = responseDataList?.days
                        val rootView = findViewById<ViewGroup>(android.R.id.content)
                        stopShimmerForAllShimmerFrames(rootView)
                        updateUI(mobil, search, total, days)
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@DetailMobilActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MobilDetail>, t: Throwable) {
                    Log.e("SearchMobil", t.stackTraceToString())
                    Toast.makeText(this@DetailMobilActivity, "An error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }
    // Function to apply shimmer animation to all ShimmerFrameLayout views in the layout
    private fun applyShimmerToAllShimmerFrames(viewGroup: ViewGroup) {
        applyShimmerToViewGroup(viewGroup)
    }

    // Recursive function to apply shimmer animation to all ShimmerFrameLayout views in a ViewGroup
    private fun applyShimmerToViewGroup(viewGroup: ViewGroup) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)
            if (childView is ShimmerFrameLayout) {
                visibleChild(childView)
                childView.startShimmer()
            } else if (childView is ViewGroup) {
                applyShimmerToViewGroup(childView)
            }
        }
    }

    private fun visibleChild(viewGroup: ViewGroup){
        val childCount = viewGroup.childCount
        for(i in 0 until childCount){
            val childView = viewGroup.getChildAt(i)
            if(childView is LinearLayout){
                childView.visibility = View.VISIBLE
            }else if(childView is ViewGroup){
                goneChild(childView)
            }
        }
    }

    // Function to stop shimmer animation for all ShimmerFrameLayout views in the layout
    private fun stopShimmerForAllShimmerFrames(viewGroup: ViewGroup) {
        stopShimmerForViewGroup(viewGroup)
    }

    // Recursive function to stop shimmer animation and hide the <include> view in a ViewGroup
    private fun stopShimmerForViewGroup(viewGroup: ViewGroup) {
        val childCount = viewGroup.childCount
        for (i in 0 until childCount) {
            val childView = viewGroup.getChildAt(i)
            if (childView is ShimmerFrameLayout) {
                childView.stopShimmer()
                childView.hideShimmer()
                goneChild(childView)
            } else if (childView is ViewGroup) {
                stopShimmerForViewGroup(childView)
            }
        }
    }

    private fun goneChild(viewGroup: ViewGroup){
        val childCount = viewGroup.childCount
        for(i in 0 until childCount){
            val childView = viewGroup.getChildAt(i)
            if(childView is LinearLayout && childView.tag == "hide"){
                childView.visibility = View.GONE
            }else if(childView is ViewGroup){
                goneChild(childView)
            }
        }
    }


    private fun showDialog(){
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.bottomsheetlllayout)
        val jmlhHari = dialog.findViewById<TextView>(R.id.total_hari)
        val hargaHari = dialog.findViewById<TextView>(R.id.harga_hari)
        val totalHarga = dialog.findViewById<TextView>(R.id.harga_total)

        val btnClose = dialog.findViewById<ImageView>(R.id.close_btn)

        btnClose.setOnClickListener{
            dialog.hide()
        }
//
        jmlhHari.setText(totalHari)
        hargaHari.setText(harga)
        totalHarga.setText(totalHargaR)
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimation
        dialog.window?.setGravity(Gravity.BOTTOM)

        dialog.show()
    }

    private fun updateUI(mobil: MobilClass?, search: Search?, total: Int?, days: Int?){
        toolbar.setTitle(mobil?.nama)
        harga = mobil?.harga.toString()
        val hargaTot = formatNumber(total)
        val gambar_rl = "https://cloudrental.my.id/storage/" + mobil?.gambar
        Picasso.get()
            .load(gambar_rl)
            .into(gambar)
        totalHarga.setText("IDR " + hargaTot)
        totalHargaR = hargaTot
        totalHari = days.toString()
        brand.setText(mobil?.brand)
        manual.setText(mobil?.transmisi)
        seat.setText(mobil?.seat + " seat")
        mesin.setText(mobil?.mesin + " CC")
    }

    private fun formatNumber(number: Int?): String{
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        googleMap.uiSettings.isZoomControlsEnabled = true

        val kantor = LatLng(-7.428603138510555, 109.2768924727306)
        googleMap.addMarker(MarkerOptions().position(kantor).title("Marker in Kantor"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kantor, 12f))
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
}