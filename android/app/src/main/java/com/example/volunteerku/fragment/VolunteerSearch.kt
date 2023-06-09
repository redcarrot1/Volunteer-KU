import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.volunteerku.data.response
import com.example.volunteerku.databinding.ActivityVolunteerSearchBinding
import com.example.volunteerku.fragment.SearchAdapter
import com.example.volunteerku.service.VolunteerDataInterface
import com.tickaroo.tikxml.TikXml
import com.tickaroo.tikxml.retrofit.TikXmlConverterFactory
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class VolunteerSearch : Fragment() {

    lateinit var binding: ActivityVolunteerSearchBinding
    private val volunteerDataList: MutableList<response> = mutableListOf()
    private lateinit var searchAdapter: SearchAdapter

    var searchKeyword = ""
    var searchArea = ""
    var searchStartDate = ""
    var searchEndDate = ""
    var StartDateString = ""
    var EndDateString = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ActivityVolunteerSearchBinding.inflate(inflater, container, false)

        volunteerSearch()
        binding.editTextTextStartDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                    StartDateString = sdf.format(selectedDate.time)
                    binding.editTextTextStartDate.text = StartDateString
                    //  updateRegisterButtonState()
                }
            DatePickerDialog(
                requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                    Calendar.MONTH
                ), cal.get(
                    Calendar.DAY_OF_MONTH
                )
            ).show()
        }
        binding.editTextVolunteerEndDate.setOnClickListener {
            val cal = Calendar.getInstance()
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(Calendar.YEAR, year)
                    selectedDate.set(Calendar.MONTH, month)
                    selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
                    EndDateString = sdf.format(selectedDate.time)
                    binding.editTextVolunteerEndDate.text = EndDateString
                    //  updateRegisterButtonState()
                }
            DatePickerDialog(
                requireContext(), dateSetListener, cal.get(Calendar.YEAR), cal.get(
                    Calendar.MONTH
                ), cal.get(
                    Calendar.DAY_OF_MONTH
                )
            ).show()
        }

        binding.searchBtn.setOnClickListener {
            searchKeyword = binding.editTextTextVolunteerName.text.toString()
            searchArea = binding.editTextVolunteerLocation.text.toString()
            searchStartDate = binding.editTextTextStartDate.text.toString()
            searchEndDate = binding.editTextVolunteerEndDate.text.toString()
            volunteerSearch(searchKeyword, searchArea, searchStartDate, searchEndDate)
        }
        searchAdapter = SearchAdapter()

        binding.searchRecyclerView.adapter = searchAdapter
        return binding.root
    }


    private fun volunteerSearch(
        keyword: String = "",
        area: String = "",
        startDate: String = "",
        endDate: String = ""
    ) {

        val client = OkHttpClient()
        val parser = TikXml.Builder().exceptionOnUnreadXml(false).build()
        val url = "http://openapi.1365.go.kr"
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(TikXmlConverterFactory.create(parser))
            .baseUrl(url)
            .build()
            .create(VolunteerDataInterface::class.java)


        retrofit.volunteerSearch(startDate, endDate, keyword, area)
            .enqueue(object : retrofit2.Callback<response> {
                override fun onResponse(call: Call<response>, response: Response<response>) {
                    if (!response.isSuccessful) {
                        try {
                            throw IOException("Unexpected code $response")
                        } catch (e: Exception) {
                            println("volunteerSearch ${e.message}")
                        }
                    }

                    val responseBody = response.body()
                    println("responseBody $responseBody")
                    responseBody?.let {
                        activity?.runOnUiThread {
                            searchAdapter.submitList(it.body?.items?.item ?: emptyList())
                        }
                    }
                }

                override fun onFailure(call: Call<response>, t: Throwable) {
                    Log.i("VolunteerSearch", "VolunteerSearch ${t.message}")
                }

            })
    }

}


