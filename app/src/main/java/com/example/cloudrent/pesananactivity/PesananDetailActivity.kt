package com.example.cloudrent.pesananactivity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cloudrent.R
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Invoice
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.response.ResponsePesananShow
import com.example.cloudrent.response.ResponseUploadBukti
import com.google.android.material.appbar.MaterialToolbar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale


class PesananDetailActivity : AppCompatActivity() {
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
    private lateinit var btnUngah: Button
    private lateinit var token: String
    private var selectedImageUri: Uri? = null
    private val READ_EXTERNAL_STORAGE_PERMISSION = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private val REQUEST_PERMISSION_CODE = 123
    private lateinit var kode_pesanan: String
    private lateinit var progressBarButton: CardView
    private lateinit var btn_text_progress: TextView
    private lateinit var progress: LinearLayout
    private lateinit var errorMessage: LinearLayout


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pesanan_detail)

        kode_pesanan = intent.getStringExtra("kode_pesanan").toString()
        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        token = sharedPreferences.getString("AuthToken", "") ?: ""

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
        btnUngah = findViewById(R.id.btnUnggah)
        progressBarButton = findViewById(R.id.button_proses_validasi)
        btn_text_progress = findViewById(R.id.text_button_progress)
        progress = findViewById(R.id.progressBar)
        errorMessage = findViewById(R.id.error_text)

        pesananShow(token, kode_pesanan)

        btnUngah.setOnClickListener {
            openImagePicker()
        }
    }

    private fun openImagePicker() {
        if (ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE_PERMISSION) == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, proceed with opening the image picker
            openImagePickerInternal()
        } else {
            // Permission not granted, request it from the user
            ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE_PERMISSION), REQUEST_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(grantResults.isNotEmpty()&& grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openImagePickerInternal()
        }else{
            Toast.makeText(this@PesananDetailActivity, "Buka Permition Untuk Upload Gambar", Toast.LENGTH_LONG).show()
        }
    }

    private fun openImagePickerInternal(){
        val intent = Intent(Intent.ACTION_PICK).also {
            it.type = "image/**"
            val mimeType = arrayOf("image/jpeg", "image/png", "image/jpg")
            it.putExtra(Intent.EXTRA_MIME_TYPES, mimeType)
            startActivityForResult(it, PICK_IMAGE_REQUEST)
        }
    }

    companion object{
        const val PICK_IMAGE_REQUEST = 101
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            when(requestCode){
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                    val imagePath = selectedImageUri?.let { getFilePath(selectedImageUri!!) }
                    uploadBukti(token, imagePath)
                }
            }
        }
    }

    private fun getFilePath(uri: Uri):String?{
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if(it.moveToFirst()){
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return  it.getString(columnIndex)
            }
        }
        return null
    }

    private fun uploadBukti(token: String, selectedUri: String?){
        val file = File(selectedUri)
        val mediaType = "multipart/form-data".toMediaTypeOrNull()
        val requestFile = RequestBody.create(mediaType, file)
        val imagePart = MultipartBody.Part.createFormData("gambar", file.name, requestFile)
        val apiService = ApiClient.create(token)
        apiService.uploadBukti(imagePart).enqueue(object: Callback<ResponseUploadBukti>{
            override fun onResponse(
                call: Call<ResponseUploadBukti>,
                response: Response<ResponseUploadBukti>
            ) {
                if(response.isSuccessful){
                    val responseMessage = response.body()?.message
                    pesananShow(token, kode_pesanan)
                }else{
                    val errorMessage = response.body()?.message
                    Toast.makeText(this@PesananDetailActivity, "errorMessage", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseUploadBukti>, t: Throwable) {
                Log.e("SearchMobil", t.stackTraceToString())
                Toast.makeText(this@PesananDetailActivity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun pesananShow(token: String, kode_pesanan: String)
    {
        progress.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
        val apiService = ApiClient.create(token)
        apiService.pesananShow(kode_pesanan).enqueue(object : Callback<ResponsePesananShow>{
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
                    Toast.makeText(this@PesananDetailActivity, "Terjadi Kesalahan", Toast.LENGTH_LONG).show()
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

        val status_id = invoice?.status?.id
        if(status_id == 3)
        {
            btnUngah.visibility = View.GONE
            progressBarButton.visibility = View.VISIBLE
            btn_text_progress.setText("PROSES VALIDASI...")
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