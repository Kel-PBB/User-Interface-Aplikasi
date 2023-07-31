package com.example.cloudrent.fragment

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.cloudrent.Login
import com.example.cloudrent.R
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.ResponseLogout
import com.example.cloudrent.response.ResponseUserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var userName: EditText
    private lateinit var userUsername: EditText
    private lateinit var userEmail: EditText
    private lateinit var btnLogout: Button
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressBar: LinearLayout
    private lateinit var errorText: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        userName = view.findViewById(R.id.user_nama)
        userUsername = view.findViewById(R.id.user_username)
        userEmail = view.findViewById(R.id.user_email)
        btnLogout = view.findViewById(R.id.btnLogout)
        progressBar = view.findViewById(R.id.progressBar)
        errorText = view.findViewById(R.id.error_text)

        val sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AuthToken", "") ?: ""
        userProfile(token)
        btnLogout.setOnClickListener{
            showExitConfirmationDialog(token)
        }
        return view
    }

    private fun userProfile(token: String) {
        errorText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.userProfile().enqueue(object : Callback<ResponseUserProfile> {
            override fun onResponse(
                call: Call<ResponseUserProfile>,
                response: Response<ResponseUserProfile>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val responseDataList = response.body()
                    updateUi(responseDataList)
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseUserProfile>, t: Throwable) {
                progressBar.visibility = View.GONE
                errorText.visibility = View.VISIBLE
                Log.e("UserProfile", t.stackTraceToString())
                Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun logout(token: String)
    {
        val apiService = ApiClient.create(token)
        apiService.logout().enqueue(object : Callback<ResponseLogout>{
            override fun onResponse(
                call: Call<ResponseLogout>,
                response: Response<ResponseLogout>
            ) {
                if(response.isSuccessful){
                    val responseLogout = response.body()?.message
                    val sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
                    val ediSaveLogin = sharedPreferences.edit()
                    ediSaveLogin.remove("Login").apply()
                    ediSaveLogin.clear().apply()
                    val message = "Log Out Berhasil"
                    val intent = Intent(requireContext(), Login::class.java)
                    intent.putExtra("toastMessage", message)
                    startActivity(intent)
                }
            }
            override fun onFailure(call: Call<ResponseLogout>, t: Throwable) {
//                progressBar.visibility = View.GONE
//                errorText.visibility = View.VISIBLE
                Log.e("UserProfile", t.stackTraceToString())
                Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun showExitConfirmationDialog(token: String) {
        val alertDialog = AlertDialog.Builder(requireContext())

        alertDialog.setTitle("Logout")
        alertDialog.setMessage("Apa Anda yakin ingin keluar??")
        alertDialog.setPositiveButton("Ya") { dialog: DialogInterface, _: Int ->
            logout(token)
        }
        alertDialog.setNegativeButton("Tidak") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    private fun updateUi(responDataList: ResponseUserProfile?){
        userName.setText(responDataList?.profile?.name)
        userUsername.setText(responDataList?.profile?.username)
        userEmail.setText(responDataList?.profile?.email)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}