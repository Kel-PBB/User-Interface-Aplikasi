package com.example.cloudrent

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.databinding.ActivityHomeBinding
import com.example.cloudrent.fragment.HomeFragment
import com.example.cloudrent.fragment.CallCenterFragment
import com.example.cloudrent.fragment.NotificcationsFragment
import com.example.cloudrent.fragment.ProfileFragment

class HomeActivity : AppCompatActivity(), HomeFragment.DataPassListener {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var recyclerView: RecyclerView
    private var isLoggedIn = false
    private var isBackPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomeFragment())

        val sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE)
        val ediSaveLogin = sharedPreferences.edit()

        if (sharedPreferences.getString("Status", "Off") == "On") {
            isLoggedIn = true
        }


        binding.bottonnav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.navigation_home -> replaceFragment(HomeFragment())
                R.id.navigation_callcenter -> replaceFragment(CallCenterFragment())
                R.id.navigation_notifications -> replaceFragment(NotificcationsFragment())
                R.id.navigation_profile -> replaceFragment(ProfileFragment())
                else -> {

                }
            }
            true
        }
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

    override fun onDataPass(token: String, tanggal_mulai: String, tanggal_selesai: String, waktu: String) {
        // Start the SearchActivity and pass the data
        val intent = Intent(this, SearchActivity::class.java).apply {
            putExtra("token", token)
            putExtra("tanggal_mulai", tanggal_mulai)
            putExtra("tanggal_selesai", tanggal_selesai)
            putExtra("waktu", waktu)
        }
        startActivity(intent)
    }

    override fun onBackPressed() {
        if (isLoggedIn) {
            if (isBackPressedOnce) {
                // Close the application
                super.onBackPressed()
            } else {
                // Show a warning dialog
                showExitConfirmationDialog()
            }
        } else {
            super.onBackPressed()
        }
    }

    private fun showExitConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Exit Application")
        alertDialog.setMessage("Are you sure you want to close the application?")
        alertDialog.setPositiveButton("Yes") { dialog: DialogInterface, _: Int ->
            isBackPressedOnce = true
            dialog.dismiss()
            finishAffinity()
        }
        alertDialog.setNegativeButton("No") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    fun onSomeButtonClicked(view: View) {
        // Handle button click
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.nav_host, fragment)
        fragmentTransaction.commit()
    }
}