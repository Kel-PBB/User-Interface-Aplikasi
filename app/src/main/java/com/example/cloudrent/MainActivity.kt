package com.example.cloudrent

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private var isLoggedIn = false
    private var isBackPressedOnce = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lets_go)

        val btnLet: Button = findViewById(R.id.btnLet)
        btnLet.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val ediSaveLogin = sharedPreferences.edit()

        if(sharedPreferences.getString("Status", "Off") == "On"){
            isLoggedIn = true
            finish()
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }


    fun onTextClicked(view: View) {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}