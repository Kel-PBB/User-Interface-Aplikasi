package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.*
import com.example.cloudrent.variabel.PemesanVariabel
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class FormPesananActivity : AppCompatActivity() {

    private lateinit var progressBar: LinearLayout
    private lateinit var totalHarga: TextView
    private lateinit var tglMulai: TextView
    private lateinit var tglSelesai: TextView
    private lateinit var namaMobil: TextView
    private lateinit var hari: TextView
    private lateinit var waktu: TextView
    private lateinit var namaPemesan: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var btnPesan: Button
    private lateinit var noHp: TextInputEditText
    private lateinit var btnUserAddPemesan: CardView
    private var newPemesan: PemesanVariabel? = null
    private lateinit var errorNama: TextView
    private lateinit var errorNomor: TextView
    private lateinit var errorEmail: TextView

    private lateinit var wktu: String
    private lateinit var tgl_r_m: String
    private lateinit var tgl_r_s: String
    private lateinit var ttl: String
    private lateinit var ssts_id: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_pesanan)

        val toolbar: Toolbar = findViewById(R.id.toolbar_form_pesanan)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationIcon(R.drawable.baseline_keyboard_arrow_left_24)
        toolbar.navigationIcon?.setTint(ContextCompat.getColor(this, R.color.white))
        toolbar.setNavigationOnClickListener(){
            onBackPressed()
        }
        toolbar.setTitleTextColor(Color.WHITE)

        totalHarga = findViewById(R.id.harga_total)
        hari = findViewById(R.id.total_hari)
        tglMulai = findViewById(R.id.pesan_tgl_mulai)
        tglSelesai = findViewById(R.id.pesan_tgl_selesai)
        namaMobil = findViewById(R.id.nama_mobil)
        waktu = findViewById(R.id.jam)
        namaPemesan = findViewById(R.id.nama_pemesan)
        email = findViewById(R.id.email)
        progressBar = findViewById(R.id.progressBar_detail)
        btnPesan = findViewById(R.id.btnPesan)
        noHp = findViewById(R.id.no_hp)
        btnUserAddPemesan = findViewById(R.id.btnUserAddAsPemesan)
        errorEmail = findViewById(R.id.textErrorEmail)
        errorNama = findViewById(R.id.textErrorName)
        errorNomor = findViewById(R.id.textErrorNomor)

        val token = intent.getStringExtra("token").toString()
        val nama_mobil = intent.getStringExtra("nama_mobil").toString()
        val mobil_id = intent.getStringExtra("mobil_id").toString()

        namaMobil.setText(nama_mobil)

        pesanForm(token)

        btnUserAddPemesan.setOnClickListener {
            newPemesan?.let { pemesanVariabel ->
                namaPemesan.setText(pemesanVariabel.nama)
                email.setText(pemesanVariabel.email)
            }
        }

        btnPesan.setOnClickListener {
            errorNomor.visibility = View.GONE
            errorEmail.visibility = View.GONE
            errorNama.visibility = View.GONE
            val nama_pemesan = namaPemesan.text.toString()
            val id_mobil = mobil_id.toInt()
            val waktuu = parseTimeStringTo(wktu)
            val jam = waktuu
            val t_r_m = tgl_r_m
            val t_r_s = tgl_r_s
            val ttl = ttl
            val sts = 1
            val email_p = email.text.toString()
            val no_hp = noHp.text.toString()
            if(namaPemesan.toString().isNotEmpty() && email.toString().isNotEmpty() && noHp.toString().isNotEmpty()){
                tambahPesanan(token, id_mobil, jam, t_r_m, t_r_s, ttl, sts, email_p, no_hp, nama_pemesan)
            }
        }
    }

    private fun tambahPesanan(token: String, mobil_id: Int?, waktu_pengambilan: String?, tanggal_rental_mulai: String?, tanggal_rental_selesai: String?, total: String?, status_id: Int?, email_pemesan: String?, no_hp: String?, nama_pemesan: String?){
        val apiClient = ApiClient.create(token)
        apiClient.tambahPesanan(mobil_id, waktu_pengambilan, tanggal_rental_mulai, tanggal_rental_selesai, total, status_id, email_pemesan, no_hp, nama_pemesan)
            .enqueue(object : Callback<ResponseTambahPesanan>{
            override fun onResponse(call: Call<ResponseTambahPesanan>, response: Response<ResponseTambahPesanan>) {
                if (response.isSuccessful){
                    val message = response.body()?.message
                    val intent = Intent(this@FormPesananActivity, SuccessActivity::class.java)
                    startActivity(intent)
                }else{
                    val errorResponse = response.errorBody()?.string()
                    val errorJson = JSONObject(errorResponse)
                    val errors = errorJson.getJSONObject("errors")
                    for (key in errors.keys())
                    {
                        val errorArray = errors.getJSONArray(key)
                        val errorMessage = errorArray.getString(0)

                        val textViewError = when (key) {
                            "email_pemesan" -> findViewById<TextView>(R.id.textErrorEmail)
                            "no_hp" -> findViewById<TextView>(R.id.textErrorNomor)
                            "nama_pemesan" -> findViewById<TextView>(R.id.textErrorName)
                            else -> null
                        }
                        textViewError?.text = errorMessage
                        textViewError?.visibility = View.VISIBLE
                    }
                }
            }
                override fun onFailure(call: Call<ResponseTambahPesanan>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("SearchMobil", t.stackTraceToString())
                    Toast.makeText(this@FormPesananActivity, "An error occurred", Toast.LENGTH_SHORT).show()
                }
        })

    }

    private fun pesanForm(token: String) {
        progressBar.visibility = View.VISIBLE
        ApiClient.create(token).pesanForm().enqueue(object : Callback<ResponseFormPesan> {
            override fun onResponse(call: Call<ResponseFormPesan>, response: Response<ResponseFormPesan>) {
                if (response.isSuccessful) {
                    val responseDataList = response.body()

                    val user = responseDataList?.user
                    val kode_mobil = responseDataList?.kode_mobil
                    val search = responseDataList?.search
                    val total = responseDataList?.total
                    val days = responseDataList?.days

                    ttl = total.toString()
                    tgl_r_m = search!!.tanggal_mulai
                    tgl_r_s = search!!.tanggal_selesai
                    wktu = search!!.jam_pengembalian

                    updateUI(kode_mobil, search, user, total, days)

                    progressBar.visibility = View.GONE

                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(this@FormPesananActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseFormPesan>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("SearchMobil", t.stackTraceToString())
                Toast.makeText(this@FormPesananActivity, "An error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun updateUI(kode_mobil: String?, search: Search?, user: User?, total: Int?, days: Int?){
        val hargaTot = formatNumber(total)
        totalHarga.setText(hargaTot)
        hari.setText(days.toString())
        val tglM = parseDate(search!!.tanggal_mulai)
        val tglS = parseDate(search!!.tanggal_selesai)
        val jam = parseTimeString(search!!.jam_pengembalian)

//        namaPemesan.setText(user!!.name)
//        email.setText(user!!.email)
        newPemesan = PemesanVariabel(user?.name ?:"", user?.email ?:"")

        tglMulai.setText(tglM)
        tglSelesai.setText(tglS)
        waktu.setText(jam)
    }

    private fun formatNumber(number: Int?): String{
        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
        return numberFormat.format(number)
    }

    private  fun parseDate(dateString: String): String
    {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.getDefault())

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

    fun parseTimeStringTo(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss:SSS", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val time: Date = inputFormat.parse(timeString) ?: return ""
        val parsedTime = outputFormat.format(time)

        return "$parsedTime"
    }
}