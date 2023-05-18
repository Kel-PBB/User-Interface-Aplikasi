package com.example.cloudrent

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }
    fun onTextClicked(view: View) {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
    }
}