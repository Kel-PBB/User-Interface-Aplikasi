package com.example.cloudrent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.logging.Handler

@Suppress("DEPRECATION")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        android.os.Handler().postDelayed({
            val pindah = Intent(this, Login::class.java)
            startActivity(pindah)
            finish()
        }, 2000)// ketika aplikasi pertama kli dibuka dia menuju splashcreen dan akan delay
        // selama 2000s kmdn lgsg menuju halaman login
        //agar saat program dirun munculnya di splass page, kita hrs menyettingnya di manifest
    }
}
