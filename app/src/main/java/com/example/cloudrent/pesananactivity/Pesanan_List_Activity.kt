package com.example.cloudrent.pesananactivity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cloudrent.R
import com.example.cloudrent.adapter.MenuItemPesananAdapter
import com.example.cloudrent.adapter.PesananListAdapter
import com.example.cloudrent.adapter.SkeletonAdapter
import com.example.cloudrent.adapter.SkeletonMenuAdapter
import com.example.cloudrent.databinding.ActivityPesananListBinding
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.response.ResponsePesanans
import com.example.cloudrent.variabel.MenuVariabel
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Response

class Pesanan_List_Activity : AppCompatActivity() {
    private lateinit var binding: ActivityPesananListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerViewMenu: RecyclerView
    private lateinit var adapter: PesananListAdapter
    private lateinit var adaptermenu: MenuItemPesananAdapter
    private lateinit var progressBar: LinearLayout
    private lateinit var errorText: LinearLayout
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var dataPesanans: ArrayList<Pesanans>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var shimmer_layout: ShimmerFrameLayout
    private lateinit var rvSkeleton: RecyclerView
    private lateinit var rvSkeletonMenu: RecyclerView
    private lateinit var shimmer_menu_layout: ShimmerFrameLayout


    private lateinit var DataMenuJudul: ArrayList<MenuVariabel>

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPesananListBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.error_text)
        swipe = binding.swipePesanan
        recyclerView = findViewById(R.id.recyclerView_pesanan)
        shimmer_layout = findViewById(R.id.shimmer_layout)
        shimmer_menu_layout = findViewById(R.id.shimmer_layout_menu)
        rvSkeleton = findViewById(R.id.recyclerViewSkeleton)
        rvSkeletonMenu = findViewById(R.id.rvMenuSkeleton)

        val skeletenMenuItem = 5
        rvSkeletonMenu.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapterSkeletonMenu = SkeletonMenuAdapter(skeletenMenuItem)
        rvSkeletonMenu.adapter = adapterSkeletonMenu
        shimmer_menu_layout.startShimmer()

        val skeletonItemCount = 5
        rvSkeleton.layoutManager = LinearLayoutManager(this)
        val adapterSkeleton = SkeletonAdapter(skeletonItemCount)
        rvSkeleton.adapter = adapterSkeleton
        Log.d("Debug", "Number of child views: ${shimmer_layout.childCount}") // Check the number of child views
        shimmer_layout.startShimmer()

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar_pesanan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_arrow_left_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        toolbar.setNavigationOnClickListener() {
            onBackPressed()
        }

        val token = intent.getStringExtra("token").toString()

        dataPesanans = ArrayList()
        DataMenuJudul = ArrayList()
        pesanList(token)

//        val pesananListAdapter = PesananListAdapter(this@Pesanan_List_Activity, arrayListOf())
        recyclerViewMenu = findViewById(R.id.menu_pesanan)
        recyclerViewMenu.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewMenu.layoutManager = layoutManager


        swipe.setOnRefreshListener {
            pesanList(token)
            swipe.isRefreshing = false
        }


        adapter = PesananListAdapter(this@Pesanan_List_Activity, arrayListOf())
        binding.recyclerViewPesanan.adapter = adapter
        binding.recyclerViewPesanan.setHasFixedSize(true)
        binding.recyclerViewPesanan.setHasFixedSize(true)
    }

    private fun countPerStatus(data: ArrayList<Pesanans>){
        val semua = data.count()
        val filteredPesanan_1 = data.filter { pesanans -> pesanans.status_id == "1"}.count()
        val filteredPesanan_2 = data.filter { pesanans -> pesanans.status_id == "2"}.count()
        val filteredPesanan_3 = data.filter { pesanans -> pesanans.status_id == "3"}.count()
        val filteredPesanan_4 = data.filter { pesanans -> pesanans.status_id == "4"}.count()

        val menuVar = listOf<MenuVariabel>(
            MenuVariabel(
                menu = "Semua",
                id = 1,
                jumlah = semua,
                isSelected = true
            ),MenuVariabel(
                menu = "Konfirmasi",
                id = 2,
                jumlah = filteredPesanan_1
            ),MenuVariabel(
                menu = "Pembayaran",
                id = 3,
                jumlah = filteredPesanan_2
            ),MenuVariabel(
                menu = "Penjemputan",
                id = 4,
                jumlah = filteredPesanan_3
            ),MenuVariabel(
                menu = "Selesai",
                id = 5,
                jumlah = filteredPesanan_4
            )
        )

        DataMenuJudul = ArrayList(menuVar)

    }

    private fun pesanList(token: String) {
//        errorText.visibility = View.GONE
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
                            dataPesanans = ArrayList(responseDataList)
                            countPerStatus(dataPesanans)
                            adaptermenu = MenuItemPesananAdapter(this@Pesanan_List_Activity, DataMenuJudul, dataPesanans, adapter){
                                    clickMenu -> clickMenu.isSelected = !clickMenu.isSelected

                                    val clickedPosition = DataMenuJudul.indexOf(clickMenu)
                                    adaptermenu.updateSelectedMenu(clickedPosition)
                                    val backgoundColor = if(clickMenu.isSelected){
                                        ContextCompat.getColor(this@Pesanan_List_Activity,
                                            R.color.white
                                        )
                                    }else{
                                        ContextCompat.getColor(this@Pesanan_List_Activity,
                                            R.color.birtud_trans
                                        )
                                    }
                                    val clickedViewHolder = binding.recyclerViewPesanan.findViewHolderForAdapterPosition(clickedPosition) as? MenuItemPesananAdapter.MyViewHolder
                                    clickedViewHolder?.card_menu?.setCardBackgroundColor(backgoundColor)

                                    val filteredPesanan = when (clickMenu.id){
                                        1 -> dataPesanans
                                        2 -> dataPesanans.filter { pesanans -> pesanans.status_id == "1"  }
                                        3 -> dataPesanans.filter { pesanans -> pesanans.status_id == "2"  }
                                        4 -> dataPesanans.filter { pesanans -> pesanans.status_id == "3"  }
                                        5 -> dataPesanans.filter { pesanans -> pesanans.status_id == "4"  }
                                        else -> {
                                            emptyList<Pesanans>()
                                        }
                                }
                                adapter.setData(filteredPesanan)
                            }
                            recyclerViewMenu.adapter = adaptermenu
                            setDataToAdapter(responseDataList)
                            recyclerViewMenu.visibility = View.VISIBLE
                            recyclerView.visibility = View.VISIBLE
                            shimmer_menu_layout.stopShimmer()
                            shimmer_menu_layout.visibility = View.GONE
                            shimmer_layout.stopShimmer()
                            shimmer_layout.visibility = View.GONE
                        }
                    } else {
                        val errorMessage = response.errorBody()?.string()
                        Toast.makeText(this@Pesanan_List_Activity, errorMessage, Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponsePesanans>, t: Throwable) {
//                    progressBar.visibility = View.GONE
                    errorText.visibility = View.VISIBLE
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