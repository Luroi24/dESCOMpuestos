package com.example.descompuestos

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sqrt

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class VisitedFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var database: MMDatabase


    //val dataList = listOf("toReview", "toReview2", "toReview3","toReview4", "toReview5", "toReview6")

    private val dataListU = mutableSetOf<Pair<String,Int>>()
    private var dataList = mutableListOf<Pair<String,Int>>()

    override fun onCreate(savedInstanceState: Bundle?) {



        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.i("Size of DATA", dataListU.size.toString())



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

        // Setting up the list of pending to review places
        val reviewsListView = view.findViewById<ListView>(R.id.listOfPlaces)




        var testing : String = "";
        var x : List<LocationEntity>? = null;

        val preferences : SharedPreferences? = this.activity?.getSharedPreferences("AppData",0);
        if(preferences != null){
            testing = preferences.getString("test",null).toString()
        }
        var gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Store?>?>() {}.getType()


        val pruebaEnFragmento : ArrayList<Store> = gson.fromJson(testing,type)


        database = Room.databaseBuilder(requireActivity().applicationContext, MMDatabase::class.java, "coordinates").build()
        lifecycleScope.launch(Dispatchers.IO){
            x = database.locationDao().getAllLocations()
            Log.i("visited",x?.get(0).toString())
            x?.forEach{
                    it1->
                pruebaEnFragmento.forEach {
                        it->
                    var distance = sqrt((it.coordinates.first-it1.latitude)*(it.coordinates.first-it1.latitude) - (it.coordinates.second-it1.longitude)*(it.coordinates.second-it1.longitude))
                    Log.i("distance", distance.toString())
                    if( distance.toLong() <= 0.5){
                        Log.i("added to list",it.storeName)
                        dataListU.add(Pair(it.storeName,it.idPlace.toInt()))
                    }else{
                        Log.i("no","no")
                    }

                }
            }
            dataListU.forEach {
                    it->
                dataList.add(it)
            }

        }



        // Setting up list of visited places
        val visitedListView = view.findViewById<ListView>(R.id.listOfPlacesReviewed)

        val db = Firebase.firestore
        val user = Firebase.auth
        val userID = user.currentUser?.uid
        //val userID = "1zZN5372jIRzwZCKyEfNGIM8weQ2"
        var infoListed = mutableListOf<Pair<String?,Double?>>()
        var nameP: String? = null;
        var reviewV : Double? = null;

        db.collection("reviews").whereEqualTo("userID",userID).get().addOnSuccessListener {
                task ->
            for(document in task){

                db.collection("places").whereEqualTo("id",document.data.get("placeID").toString().toInt()).get().addOnSuccessListener { it1 ->
                    for(document1 in it1){
                        nameP = document1.data.get("name").toString()
                        Log.i("nAME",nameP.toString())
                    }
                    reviewV = document.data.get("rating") as Double
                    Log.i("RATING",reviewV.toString())
                    var x = Pair(nameP,reviewV)
                    infoListed.add(x)
                    val reviewedAdapter = ReviewedAdapter(requireContext(),infoListed)
                    visitedListView.adapter = reviewedAdapter


                    val reviewsAdapter = CustomAdapter(requireContext(), dataList)
                    reviewsListView.adapter = reviewsAdapter

                    reviewsListView.setOnItemClickListener { parent, view, position, id ->
                        // Load the fragment based on the clicked item
                        loadFragment(position)
                    }
                }
            }
        }
    }

    fun loadFragment(position: Int){
        val bundle = Bundle()
        bundle.putInt("id", dataList[position].second)
        bundle.putString("cafeteriaTitle", dataList[position].first)
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

    class ReviewedAdapter(context: Context, private val dataList: List<Pair<String?,Double?>>) :
        ArrayAdapter<Pair<String?,Double?>>(context, 0, dataList) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.visited_lv_item_noaction, parent, false)
            }
            val textView = itemView!!.findViewById<TextView>(R.id.reviewed_placeTitle)
            val textView1 = itemView!!.findViewById<RatingBar>(R.id.reviewed_user_rating)

            textView.text = dataList[position].first.toString()
            textView1.rating = dataList[position].second!!.toFloat()
            return itemView
        }
    }

    class CustomAdapter(context: Context, private val dataList: List<Pair<String,Int>>) :
        ArrayAdapter<Pair<String,Int>>(context, 0, dataList) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var itemView = convertView
            if (itemView == null) {
                itemView = LayoutInflater.from(context).inflate(R.layout.visited_lv_item, parent, false)
            }
            val textView = itemView!!.findViewById<TextView>(R.id.visited_placeTitle)
            textView.text = dataList[position].first
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