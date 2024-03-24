package com.example.descompuestos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisitedFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    val dataList = listOf("toReview", "toReview2", "toReview3","toReview4", "toReview5", "toReview6")


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
        return inflater.inflate(R.layout.fragment_visited, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Setting up the listView
        /*
        val listView: ListView = view.findViewById(R.id.listOfPlaces)
        val adapter = context?.let { CoordinatesAdapter(it, readFileContents()) }
        listView.adapter = adapter*/
        // Setting up the list of pending to review places
        val reviewsListView = view.findViewById<ListView>(R.id.listOfPlaces)
        val reviewsAdapter = CustomAdapter(requireContext(), dataList)
        reviewsListView.adapter = reviewsAdapter
        reviewsListView.setOnItemClickListener { parent, view, position, id ->
            // Load the fragment based on the clicked item
            loadFragment(position)
        }

        // Setting up list of visited places
        val visitedListView = view.findViewById<ListView>(R.id.listOfPlacesReviewed)
        val reviewed = listOf("Reviewed", "Reviewed2", "Reviewed3","Reviewed", "Reviewed2", "Reviewed3")
        val reviewedAdapter = ReviewedAdapter(requireContext(),reviewed)
        visitedListView.adapter = reviewedAdapter
    }

    fun loadFragment(position: Int){
        val bundle = Bundle()
        bundle.putInt("id", 2)
        bundle.putString("cafeteriaTitle", dataList[position])
        val fragment = WriteRatingFragment()
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            VisitedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class ReviewedAdapter(context: Context, private val dataList: List<String>) :
        ArrayAdapter<String>(context, 0, dataList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.visited_lv_item_noaction, parent, false)
            }
            val textView = itemView!!.findViewById<TextView>(R.id.reviewed_placeTitle)
            textView.text = dataList[position]
            return itemView
        }
    }

    class CustomAdapter(context: Context, private val dataList: List<String>) :
        ArrayAdapter<String>(context, 0, dataList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.visited_lv_item, parent, false)
            }
            val textView = itemView!!.findViewById<TextView>(R.id.visited_placeTitle)
            textView.text = dataList[position]
            return itemView
        }
    }

    private class CoordinatesAdapter(context: Context, private val coordinatesList: List<List<String>>): ArrayAdapter<List<String>>(context, R.layout.listview_item, coordinatesList){
        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val view = convertView ?: inflater.inflate(R.layout.listview_item, parent, false)

            val timestampTextView: TextView = view.findViewById(R.id.tvTimestamp)
            val latitudeTextView: TextView = view.findViewById(R.id.tvLatitude)
            val longitudeTextView: TextView = view.findViewById(R.id.tvLongitude)

            try{
                val item = coordinatesList[position]
                timestampTextView.text = formatTimestamp(item[0].toLong())
                latitudeTextView.text = formatCoordinate(item[1].toDouble())
                longitudeTextView.text = formatCoordinate(item[2].toDouble())

                view.setOnClickListener{
                    val intent = Intent(context, ThirdActivity::class.java).apply{
                        putExtra("latitude", item[1])
                        putExtra("longitude", item[2])
                    }
                    context.startActivity(intent)
                }
            } catch (e: Exception){
                e.printStackTrace()
                Log.e("CoordinatesAdapter", "getView: Exception parsing coordinates.")
            }
            return view
        }

        private fun formatTimestamp(timestamp: Long): String{
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
        private fun formatCoordinate(value: Double): String {
            return String.format("%.6f", value)
        }
    }

    private fun readFileContents(): List<List<String>>{
        val fileName = "gps_coordinates.csv"
        return try {
            requireContext().openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.map { it.split(";").map(String::trim) }.toList()
            }
        }catch (e: IOException){
            listOf(listOf("Error reading file: ${e.message}"))
        }
    }
}