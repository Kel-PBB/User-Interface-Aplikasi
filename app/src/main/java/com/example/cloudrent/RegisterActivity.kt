package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.LoginRequest
import com.example.cloudrent.response.ResponseUserLogin
import com.example.cloudrent.response.ResponseUserRegister
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var inputEmail: TextInputEditText
    private lateinit var inputName: TextInputEditText
    private lateinit var inputUsername: TextInputEditText
    private lateinit var inputPassword: TextInputEditText
    private lateinit var inputPassword_Confirm: TextInputEditText
    private lateinit var btnTambah: Button

    @SuppressLint("RestrictedApi", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        toolbar.setNavigationOnClickListener(){
            onBackPressed()
        }
        toolbar.setTitleTextColor(Color.WHITE)

        inputEmail = findViewById(R.id.Email)
        inputName = findViewById(R.id.NamaLengkap)
        inputUsername = findViewById(R.id.Username)
        inputPassword = findViewById(R.id.EditPassworRegister)
        inputPassword_Confirm = findViewById(R.id.EditConfirmPasswordRegister)
        btnTambah = findViewById(R.id.daftar)
        val textViewEmail: TextView = findViewById(R.id.textErrorEmail)
        val textViewUsername: TextView = findViewById(R.id.textErrorUsername)
        val textViewPassword: TextView = findViewById(R.id.textErrorPassword)
        val textViewPaswordConfirm: TextView = findViewById(R.id.textErrorConfirmPassword)

        btnTambah.setOnClickListener {
            textViewEmail.visibility = View.GONE
            textViewUsername.visibility = View.GONE
            textViewPassword.visibility = View.GONE
            textViewPaswordConfirm.visibility = View.GONE

            val email = inputEmail.text.toString()
            val name = inputName.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            val password_confirmation = inputPassword_Confirm.text.toString()

            if(email.isNotEmpty() && name.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty() && password_confirmation.isNotEmpty()){
                insertUser(email, name, username, password, password_confirmation)
            }else{
                Toast.makeText(this@RegisterActivity, "Tolong isi datanya", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  fun insertUser(email: String, name: String, username:String, password:String, password_confirmation:String){
        val apiService = ApiClient.apiService
        val call = apiService.insertUser(email, name, username, password, password_confirmation)
        call.enqueue(object : Callback<ResponseUserRegister> {
            override fun onResponse(call: Call<ResponseUserRegister>, response: Response<ResponseUserRegister>){
                if (response.isSuccessful){
                    val responseData = response.body()
                    if(responseData != null){
                        val loginRequest = LoginRequest(email, password)
                        val logincall = apiService.loginUser(loginRequest)
                        logincall.enqueue(object : Callback<ResponseUserLogin>{
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

                                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
                                    finish()
                                }else{
                                    val errorMessage = response.errorBody()?.string()
                                    Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<ResponseUserLogin>, t: Throwable) {
                                Log.e("Insert", t.stackTraceToString())
                                Toast.makeText(this@RegisterActivity, "An error occurred",
                                    Toast.LENGTH_SHORT).show()
                            }
                        })
                    }else{
                        Toast.makeText(this@RegisterActivity, "Data Kosong", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    val errorResponse = response.errorBody()?.string()
                    val errorJson = JSONObject(errorResponse)
                    val errors = errorJson.getJSONObject("errors")
                    for (key in errors.keys())
                    {
                        val errorArray = errors.getJSONArray(key)
                        val errorMessage = errorArray.getString(0)

                        val textViewError = when (key) {
                            "email" -> findViewById<TextView>(R.id.textErrorEmail)
                            "username" -> findViewById<TextView>(R.id.textErrorUsername)
                            "password" -> findViewById<TextView>(R.id.textErrorPassword)
                            "confirmation_password" -> findViewById<TextView>(R.id.textErrorConfirmPassword)
                            else -> null
                        }
                        textViewError?.text = errorMessage
                        textViewError?.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<ResponseUserRegister>, t: Throwable) {
                Log.e("Insert", t.stackTraceToString())
                Toast.makeText(this@RegisterActivity, "An error occurred",
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun onTextClicked(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}