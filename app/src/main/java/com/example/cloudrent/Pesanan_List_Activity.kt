package com.example.cloudrent

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.adapter.PesananListAdapter
import com.example.cloudrent.adapter.SearchAdapter
import com.example.cloudrent.databinding.ActivityPesananListBinding
import com.example.cloudrent.databinding.ActivitySearchBinding
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Mobil
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.response.ResponsePesanans
import com.example.cloudrent.response.SearchResponse
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

class Pesanan_List_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityPesananListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PesananListAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_pesanan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }

        val token = intent.getStringExtra("token").toString()

        pesanList(token)

        adapter = PesananListAdapter(this@Pesanan_List_Activity, arrayListOf())
        binding.recyclerViewPesanan.adapter = adapter
        binding.recyclerViewPesanan.setHasFixedSize(true)
        binding.recyclerViewPesanan.setHasFixedSize(true)
    }

    private fun pesanList(token: String) {
//        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.pesananList().enqueue(object : retrofit2.Callback<ResponsePesanans> {
                override fun onResponse(
                    call: Call<ResponsePesanans>,
                    response: Response<ResponsePesanans>
                ) {
//                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val responseDataList = response.body()?.pesanans
                        if (responseDataList != null) {
                            setDataToAdapter(responseDataList)
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@Pesanan_List_Activity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponsePesanans>, t: Throwable) {
//                    progressBar.visibility = View.GONE
//                    errorText.visibility = View.VISIBLE
                    Log.e("SearchMobil", t.stackTraceToString())
                    Toast.makeText(this@Pesanan_List_Activity, "An error occurred", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun setDataToAdapter(data: kotlin.collections.List<Pesanans>) {
        adapter?.setData(data)
    }
}