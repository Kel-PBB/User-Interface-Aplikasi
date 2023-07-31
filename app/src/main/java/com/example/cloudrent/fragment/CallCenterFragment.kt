package com.example.cloudrent.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cloudrent.R
import com.example.cloudrent.adapter.CallCenterAdapter
import com.example.cloudrent.variabel.QuestVariabel

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CallCenterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CallCenterFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CallCenterAdapter
    private lateinit var cardView: CardView

    val DataQuest = listOf<QuestVariabel>(
        QuestVariabel(
            quest = "Cara pemesanan mobil?"
        ), QuestVariabel(
            quest = "Syarat rental mobil?"
        ), QuestVariabel(
            quest = "Tips untuk anda ketika mengambil mobil"
        )
    )

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_callcenter, container, false)
        cardView = view.findViewById(R.id.whatsapp)
        recyclerView = view.findViewById(R.id.rvPertanyaan)
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = CallCenterAdapter(requireContext(), DataQuest){}

        cardView.setOnClickListener{
            val phoneNumber = "+6281903628254"
            val message = "Hello"
            val sendIntent = Intent(Intent(Intent.ACTION_VIEW))
                .setData(Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber"))
            startActivity(sendIntent)
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CallCenterFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CallCenterFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}