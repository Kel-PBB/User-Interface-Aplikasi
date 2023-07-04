package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.adapter.SearchAdapter
import com.example.cloudrent.databinding.ActivitySeacrhBinding
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Mobil
import com.example.cloudrent.response.SearchResponse
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.material.card.MaterialCardView


class SeacrhActivity : AppCompatActivity(), HomeFragment.DataPassListener {

    private lateinit var binding: ActivitySeacrhBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_seacrh)

        val toolbar: Toolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener(){
            onBackPressed()
        }
        toolbar.setTitleTextColor(Color.WHITE)

        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener {_, verticalOffset ->
            val istoolbar = (verticalOffset == 0)
            toolbar.visibility = if(istoolbar) View.VISIBLE else View.INVISIBLE
        })

        binding = ActivitySeacrhBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = findViewById(R.id.progressBar)

        val token = intent.getStringExtra("token").toString()
        val tanggalMulai = intent.getStringExtra("tanggal_mulai").toString()
        val tanggalSelesai = intent.getStringExtra("tanggal_selesai").toString()
        val waktu = intent.getStringExtra("waktu").toString()
        searchMobil(token, tanggalMulai, tanggalSelesai, waktu)

        adapter = SearchAdapter(this@SeacrhActivity, arrayListOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setHasFixedSize(true)
    }

    override fun onDataPass(token: String, tanggal_mulai: String, tanggal_selesai: String, waktu: String){
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

    private fun searchMobil(token: String, tanggalM: String, tanggalS: String, waktu: String){
        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create("21|OQiu1ARTAfKBDPT4J8MC1DIMxcSfvBAdZ6ItAcau")
        apiService.searchMobil(tanggalM, tanggalS, waktu).enqueue(object : Callback<SearchResponse> {
            override fun onResponse(call: Call<SearchResponse>, response: Response<SearchResponse>) {
                progressBar.visibility = View.GONE
                if(response.isSuccessful){
                    val responseDataList = response.body()?.mobil
                    val search = response.body()?.search
                    if(responseDataList != null){
                        setDataToAdapter(responseDataList)
                    }

                }else{
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(this@SeacrhActivity, errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("SearchMobil", t.stackTraceToString())
                Toast.makeText(this@SeacrhActivity, "An error occurred", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setDataToAdapter(data: kotlin.collections.List<Mobil>){
        adapter?.setData(data)
    }
}