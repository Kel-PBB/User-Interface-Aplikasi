package com.example.cloudrent

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.adapter.NotifikasiAdapter
import com.example.cloudrent.adapter.PesananListAdapter
import com.example.cloudrent.network.ApiClient
import com.example.cloudrent.response.Notification
import com.example.cloudrent.response.NotificationData
import com.example.cloudrent.response.Pesanans
import com.example.cloudrent.response.ResponseNotifications
import com.example.cloudrent.response.ResponseUserProfile
import retrofit2.Call
import retrofit2.Response
import javax.security.auth.callback.Callback

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [NotificcationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificcationsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var adapter: NotifikasiAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("AuthToken", "") ?: ""
        notificationList(token)
        val view = inflater.inflate(R.layout.fragment_notificcations, container, false)
        recyclerView = view.findViewById(R.id.rvNotifications)
        adapter = NotifikasiAdapter(requireContext(), arrayListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    private fun notificationList(token: String) {
//        errorText.visibility = View.GONE
//        progressBar.visibility = View.VISIBLE
        val apiService = ApiClient.create(token)
        apiService.notificationList().enqueue(object : retrofit2.Callback<ResponseNotifications> {
            override fun onResponse(
                call: Call<ResponseNotifications>,
                response: Response<ResponseNotifications>
            ) {
//                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val responseDataList = response.body()?.notifikasi
                    if(responseDataList != null){
                        setDataToAdapter(responseDataList)
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ResponseNotifications>, t: Throwable) {
//                progressBar.visibility = View.GONE
//                errorText.visibility = View.VISIBLE
                Log.e("UserProfile", t.stackTraceToString())
                Toast.makeText(requireContext(), "An error occurred", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun setDataToAdapter(data: kotlin.collections.List<Notification>) {
        adapter?.setData(data)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment NotificcationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificcationsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}