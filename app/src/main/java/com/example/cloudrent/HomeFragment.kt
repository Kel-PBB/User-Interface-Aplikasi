package com.example.cloudrent

import android.app.Activity
import android.widget.ArrayAdapter
import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.text.InputType
import com.example.cloudrent.adapter.SearchAdapter
import com.example.cloudrent.network.ApiClient
import java.text.SimpleDateFormat
import java.util.*
import android.content.SharedPreferences
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.*
import com.example.cloudrent.adapter.SliderAdapter
import com.example.cloudrent.databinding.ActivitySeacrhBinding
import com.example.cloudrent.response.Mobil
import com.example.cloudrent.response.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters

    private lateinit var eTime: LinearLayout
    private lateinit var eTimeBox: EditText
    private lateinit var eDateMBox: EditText
    private lateinit var eDateSBox: EditText
    private lateinit var viewPager : ViewPager2
    private lateinit var spinerHari : Spinner
    private lateinit var hariVal : String
    private lateinit var dataPassListener: DataPassListener
    private var selectedValue: String? = null

    private lateinit var recyclerView: RecyclerView
    private var adapter: SearchAdapter? = null
    private lateinit var binding: ActivitySeacrhBinding

    private var param1: String? = null
    private var param2: String? = null

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById(R.id.view_pager)

        eTime = view.findViewById(R.id.waktujemputgroup)
        eDateMBox = view.findViewById(R.id.TanggalMulai)
        eDateSBox = view.findViewById(R.id.TanggalSelesai)
        eTimeBox = view.findViewById(R.id.waktujemput)
        spinerHari = view.findViewById(R.id.spinner_hari)
        val btnPesan = view.findViewById<ImageButton>(R.id.myButton)
        val spinerhari = view.findViewById<Spinner>(R.id.spinner_hari)
        val btnCari = view.findViewById<Button>(R.id.btnCari)

        eDateMBox.inputType = InputType.TYPE_NULL
        eDateSBox.inputType = InputType.TYPE_NULL
        eTimeBox.inputType = InputType.TYPE_NULL

        eDateSBox.setOnClickListener {
            showDatePicker(eDateSBox)
        }
        eTimeBox.setOnClickListener{
            showTimePicker()
        }
        eDateMBox.setOnClickListener {
            showDatePicker(eDateMBox)
        }

        eDateMBox.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed for this scenario, but you can add your code here if necessary
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not needed for this scenario, but you can add your code here if necessary
            }

            override fun afterTextChanged(s: Editable?) {
                val selectedValue = spinerhari.selectedItem.toString()
                val selectedDate = s.toString()
                if (selectedDate.isNotEmpty() && selectedValue.isNotEmpty()) {
                    val hari = selectedValue.toInt()
                    setTglS(hari, selectedDate)
                }
            }
        })

        btnPesan.setOnClickListener{
            val sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
            val token = sharedPreferences.getString("AuthToken", "") ?: ""
            val intent = Intent(requireContext(), Pesanan_List_Activity::class.java).apply {
                putExtra("token", token)
            }
            startActivity(intent)
        }



        spinerhari.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val optionsArray = resources.getStringArray(R.array.hari)
                val selectedValue = optionsArray[position]
                val selectedDate = eDateMBox.text.toString()
                val hari = selectedValue.toInt()
                hariVal = selectedValue
                if(selectedDate.isNotEmpty() && hari.toString().isNotEmpty()){
                    setTglS(hari, selectedDate)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedValue = null
                // Handle the case when nothing is selected, if needed
            }
        }


        btnCari.setOnClickListener {
            val tanggalM = eDateMBox.text.toString()
            val tanggalS = eDateSBox.text.toString()
            val waktu = eTimeBox.text.toString()

            if(tanggalM.isNotEmpty() && tanggalS.isNotEmpty() && waktu.isNotEmpty()){
                val tanggalMulai1 = parseDate(tanggalM)
                val tanggalSelesai1 = parseDate(tanggalS)
                val waktuJ1 = parseTimeString(waktu)

                val sharedPreferences = requireContext().getSharedPreferences("Login", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("AuthToken", "") ?: ""
//                startSearchActivity(token, tanggalMulai1, tanggalSelesai1, waktuJ1)
                sendDatatoActivity(token, tanggalMulai1, tanggalSelesai1, waktuJ1)
            }else{
                Toast.makeText(requireContext(), "Tolong isi datanya", Toast.LENGTH_SHORT).show()
            }
        }

        // Create a list of slide images (resource IDs)
        val slides = listOf(R.drawable.carentalppb, R.drawable.mobil3, R.drawable.mobil1)

        val sliderAdapter = SliderAdapter(slides)
        viewPager.adapter = sliderAdapter
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)
        return view
    }

    interface DataPassListener{
        fun onDataPass(token: String, tanggal_mulai: String, tanggal_selesai: String, waktu: String)
    }

    private fun setTglS(hari: Int, tgl: String){
        val inputDateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        val inputDate = inputDateFormat.parse(tgl)
        val calendar = Calendar.getInstance()
        calendar.time = inputDate
        calendar.add(Calendar.DAY_OF_MONTH, hari - 1)
        val outputDate = inputDateFormat.format(calendar.time)
        eDateSBox.setText(outputDate)
    }

    private fun getSelectedItem(){
        val spinerhari = view?.findViewById<Spinner>(R.id.spinner_hari)
        spinerhari?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val optionsArray = resources.getStringArray(R.array.hari)
                val selectedValue = optionsArray[position]
                val selectedDate = eDateMBox.text.toString()
                val hari = selectedValue.toInt()
                if(selectedDate.isNotEmpty() && hari.toString().isNotEmpty()){
                    setTglS(hari, selectedDate)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected, if needed
            }
        }
        Toast.makeText(requireContext(), "tgl", Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is DataPassListener) {
            dataPassListener = context
        } else {
            throw ClassCastException("$context must implement DataPassListener")
        }
    }

    private fun hitungTanggal()
    {

    }

    private fun sendDatatoActivity(token: String, tanggal_mulai: String, tanggal_selesai: String, waktu: String){
        dataPassListener.onDataPass(token, tanggal_mulai, tanggal_selesai, waktu)
    }

    private fun startSearchActivity(token: String, tanggal_mulai: String, tanggal_selesai: String, waktu: String) {
        val intent = Intent(requireActivity(), SeacrhActivity::class.java).apply {
            putExtra("token", token)
            putExtra("tanggal_mulai", tanggal_mulai)
            putExtra("tanggal_selesai", tanggal_selesai)
            putExtra("waktu", waktu)
        }
        startActivity(intent)
    }


    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)
        eTimeBox = view!!.findViewById(R.id.waktujemput)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                eTimeBox.setText(formattedTime)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }
    private fun showDatePicker(editText: EditText) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)
                val currentDateTime = Calendar.getInstance()

                if(editText == eDateMBox){
                    if(selectedDate.before(currentDateTime)){
                        Toast.makeText(requireContext(), "Tanggal Mulai Tidak Valid", Toast.LENGTH_SHORT).show()
                    } else{
                        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
                        val formattedDate = dateFormat.format(selectedDate.time)
                        eDateMBox.setText(formattedDate)
                    }
                }
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun getSelectedDate(editText: EditText): Calendar {
        val selectedDate = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val date = editText.text.toString()
        val parsedDate = dateFormat.parse(date)
        selectedDate.time = parsedDate
        return selectedDate
    }

    fun parseTimeString(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        val time: Date = inputFormat.parse(timeString) ?: return ""
        val parsedTime = outputFormat.format(time)

        return "$parsedTime:00"
    }

    private  fun parseDate(dateString: String): String
    {
        val inputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val date: Date = inputFormat.parse(dateString) ?: return ""
        return outputFormat.format(date)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val recyclerView: RecyclerView = view.findViewById(R.id.rv_card_mobil)
//
//        val adapter = CardCardHomeAdapter(Datamahasiswa){}
//        recyclerView.adapter = adapter
//
//        val layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        recyclerView.layoutManager = layoutManager
//
//        val marginInDp = 6 // Margin yang diinginkan dalam satuan dp
//        val itemDecoration = SpaceItemDecoration(requireContext(), marginInDp)
//        recyclerView.addItemDecoration(itemDecoration)
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}