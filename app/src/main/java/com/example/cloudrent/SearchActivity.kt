package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.adapter.SearchAdapter
import com.example.cloudrent.databinding.ActivitySeacrhBinding
import com.example.cloudrent.databinding.ActivitySearchBinding
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Mobil
import com.example.cloudrent.response.SearchResponse
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class SearchActivity : AppCompatActivity(), HomeFragment.DataPassListener {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        errorText = findViewById(R.id.error_text)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }

        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val istoolbar = (verticalOffset == 0)
            toolbar.visibility = if (istoolbar) View.VISIBLE else View.INVISIBLE
        })

        val tgl_mulai = findViewById<TextView>(R.id.tgl_mulai)
        val tgl_selesai = findViewById<TextView>(R.id.tgl_selesai)
        val jam = findViewById<TextView>(R.id.jam)

        progressBar = findViewById(R.id.progressBar)

        val token = intent.getStringExtra("token").toString()
        val tanggalMulai = intent.getStringExtra("tanggal_mulai").toString()
        val tanggalSelesai = intent.getStringExtra("tanggal_selesai").toString()
        val waktu = intent.getStringExtra("waktu").toString()

        searchMobil(token, tanggalMulai, tanggalSelesai, waktu)

        tgl_mulai.setText(parseDate(tanggalMulai))
        tgl_selesai.setText(parseDate(tanggalSelesai))
        jam.setText(parseTimeString(waktu))

        adapter = SearchAdapter(this@SearchActivity, arrayListOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setHasFixedSize(true)
    }

    override fun onDataPass(
        token: String,
        tanggal_mulai: String,
        tanggal_selesai: String,
        waktu: String
    ) {
        searchMobil(token, tanggal_mulai, tanggal_selesai, waktu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Example: Go back to the previous activity or fragment
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private  fun parseDate(dateString: String): String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

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

    private fun searchMobil(token: String, tanggalM: String, tanggalS: String, waktu: String) {
        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.searchMobil(tanggalM, tanggalS, waktu)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseDataList = response.body()?.mobil
                        val search = response.body()?.search
                        if (responseDataList != null) {
                            setDataToAdapter(responseDataList)
                        }

                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@SearchActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
                    Log.e("SearchMobil", t.stackTraceToString())
                    Toast.makeText(this@SearchActivity, "An error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun setDataToAdapter(data: kotlin.collections.List<Mobil>) {
        adapter?.setData(data)
    }
}
