package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cloudrent.adapter.SearchAdapter
import com.example.cloudrent.adapter.SkeletonAdapter
import com.example.cloudrent.databinding.ActivitySearchBinding
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Mobil
import com.example.cloudrent.response.SearchResponse
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerFrameLayout
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
    private lateinit var rvMobil: RecyclerView
    private lateinit var adapter: SearchAdapter
    private lateinit var progressBar: LinearLayout
    private lateinit var errorText: LinearLayout
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var simmer_layout: ShimmerFrameLayout
    private lateinit var rvSkeleton: RecyclerView

    @SuppressLint("MissingInflatedId", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        errorText = findViewById(R.id.error_text)
        swipe = findViewById(R.id.swipe_search)
        simmer_layout = findViewById(R.id.shimmer_layout)
        rvSkeleton = findViewById(R.id.recyclerViewSkeleton)

        rvMobil = findViewById(R.id.recyclerViewMobil)
        adapter = SearchAdapter(this@SearchActivity, arrayListOf())
        rvMobil.adapter = adapter
        rvMobil.setHasFixedSize(true)

//        rvSkeleton = findViewById(R.id.recyclerViewSkeleton)
//        rvSkeleton.adapter = adapterSkeleton
        val skeletonItemCount = 5
        rvSkeleton.layoutManager = LinearLayoutManager(this)
        val adapterSkeleton = SkeletonAdapter(skeletonItemCount)
        rvSkeleton.adapter = adapterSkeleton
        Log.d("Debug", "Number of child views: ${simmer_layout.childCount}") // Check the number of child views
        simmer_layout.startShimmer()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_search)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_arrow_left_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.birtud))
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }

//        val appBarLayout = findViewById<AppBarLayout>(R.id.appBarLayout)
//        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
//            val istoolbar = (verticalOffset == 0)
//            toolbar.visibility = if (istoolbar) View.VISIBLE else View.INVISIBLE
//        })


//        progressBar = findViewById(R.id.progressBar)

        val token = intent.getStringExtra("token").toString()
        val tanggalMulai = intent.getStringExtra("tanggal_mulai").toString()
        val tanggalSelesai = intent.getStringExtra("tanggal_selesai").toString()
        val waktu = intent.getStringExtra("waktu").toString()

        val tgl_Pencarian = parseDate(tanggalMulai) + " - " + parseDate(tanggalSelesai)
        toolbar.setSubtitle(tgl_Pencarian)

//        tgl_mulai.setText(parseDate(tanggalMulai))
//        tgl_selesai.setText(parseDate(tanggalSelesai))
//        jam.setText(parseTimeString(waktu))
        searchMobil(token, tanggalMulai, tanggalSelesai, waktu)
        swipe.setOnRefreshListener {
//            skeletonview.showSkeleton()
            searchMobil(token, tanggalMulai, tanggalSelesai, waktu)
//            swipe.isRefreshing = false
        }


//        adapter = SearchAdapter(this@SearchActivity, arrayListOf())
//        binding.recyclerView.adapter = adapter
//        binding.recyclerView.setHasFixedSize(true)
//        binding.recyclerView.setHasFixedSize(true)
//        skeletonview.showSkeleton()
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
//        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.searchMobil(tanggalM, tanggalS, waktu)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
//                    progressBar.visibility = View.GONE
//                    skeletonview.showOriginal()
                    swipe.isRefreshing = false
                    if (response.isSuccessful) {
                        val responseDataList = response.body()?.mobil
                        val search = response.body()?.search
                        if (responseDataList != null) {
                            setDataToAdapter(responseDataList)
                        }
                        rvMobil.visibility = View.VISIBLE
                        simmer_layout.stopShimmer()
                        simmer_layout.visibility = View.GONE

                    } else {
                        val errorMessage = response.errorBody()?.string()
//                        skeletonview.showOriginal()
                        Toast.makeText(this@SearchActivity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
//                    progressBar.visibility = View.GONE
//                    skeletonview.showOriginal()
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
