package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cloudrent.database.DBHelper
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.network.ApiClient.apiService
import com.example.cloudrent.response.LoginRequest
import com.example.cloudrent.response.ResponseUserLogin
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class Login : AppCompatActivity() {

    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var errorM: LinearLayout
    private lateinit var progressBar: LinearLayout

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailT = findViewById<TextInputEditText>(R.id.email_login)
        val passwordT = findViewById<TextInputEditText>(R.id.password_login)
        progressBar = findViewById(R.id.progressBar)
        val intent = intent
        if(intent.hasExtra("toastMessage")){
            val message = intent.getStringExtra("toastMessage")
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
        errorM = findViewById(R.id.errorLogin)

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val ediSaveLogin = sharedPreferences.edit()

        if(sharedPreferences.getString("Status", "Off") == "On"){
            startActivity(Intent(this, HomeActivity::class.java))
        }

        val btnLogin: Button = findViewById(R.id.btnLogin)


        btnLogin.setOnClickListener {
            val email = emailT.text.toString()
            val password = passwordT.text.toString()
            loginUser(email, password)
        }

    }

    private fun loginUser(email: String, password: String){
        progressBar.visibility = View.VISIBLE
        errorM.visibility = View.GONE
            val loginRequest = LoginRequest(email, password)
            val logincall = apiService.loginUser(loginRequest)
            logincall.enqueue(object : Callback<ResponseUserLogin> {
                override fun onResponse(call: Call<ResponseUserLogin>, response: Response<ResponseUserLogin>) {
                    if(response.isSuccessful){
                        val token = response.body()?.token

                        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("Email", email)
                        editor.putString("Password", password)
                        editor.putString("Status", "On")
                        editor.putString("AuthToken", token)
                        editor.apply()

                        startActivity(Intent(this@Login, HomeActivity::class.java))
                        finish()
                    }else{
                        val errorMessage = response.errorBody()?.string()
                        progressBar.visibility = View.GONE
                        errorM.visibility = View.VISIBLE
                    }
                }
                override fun onFailure(call: Call<ResponseUserLogin>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    Log.e("Insert", t.stackTraceToString())
                    Toast.makeText(this@Login, "Periksa Jaringan Internet Anda",
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun onTextClicked(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}