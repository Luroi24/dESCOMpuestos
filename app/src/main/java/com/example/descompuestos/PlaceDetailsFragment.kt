package com.example.descompuestos

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.ListView
import android.widget.RatingBar
import android.widget.TextView
import androidx.compose.ui.text.substring
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class PlaceDetails : Fragment() {
    var placeId: Int? = null
    var title: String? = ""
    var description: String? = ""
    var rating: String? = ""
    var latitude: Double? = null
    var longitude: Double? = null
    lateinit var placeImg: List<String>

    var reviewTest = arrayListOf<Review>()
    var dataReviews = mutableListOf<Review>();



    private fun setReviews(){
        var testing1 : String = "";
        val preferences : SharedPreferences? = this.activity?.getSharedPreferences("AppData",0);
        if(preferences != null){
            testing1 = preferences.getString("reviews",null).toString()
        }
        var gson1 = Gson()
        val type1: Type = object : TypeToken<ArrayList<Review?>?>() {}.getType()
        val reviewEnFragmento : ArrayList<Review> = gson1.fromJson(testing1,type1)
        reviewTest = reviewEnFragmento;
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setReviews();
        // Inflate the layout for this fragment
        val data = arguments
        placeId = data?.getInt("id")
        title = data?.getString("title")
        description = data?.getString("description")
        rating = data?.getString("rating")
        latitude = data?.getDouble("latitude")
        longitude = data?.getDouble("longitude")
        var aux = data?.getStringArrayList("imgUrls")
        if (aux != null) {
            placeImg = aux.toList()
        }
        Log.i("PLACEID", placeId.toString())
        dataReviews.clear()
        reviewTest.forEach {
            it->
            if(it.idPlace.toInt() == placeId){
                dataReviews.add(it);
            }
        }

        return inflater.inflate(R.layout.fragment_place_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Here's everything about the three images that show up for each place.
        val snapHelper = PagerSnapHelper()
        val placeDetailsRV: RecyclerView = view.findViewById(R.id.pd_rv_imgs)
        snapHelper.attachToRecyclerView(placeDetailsRV)
        placeDetailsRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        //val images = listOf(placeImg)
        val placeDetailsAdapter = PlaceDetailsAdapter(placeImg,R.layout.place_details_item)
        placeDetailsRV.adapter = placeDetailsAdapter

        // Setting up the correct text for each item that has been selected.
        val placeTitle: TextView = view.findViewById(R.id.pd_title)
        placeTitle.text = title

        val placeDescription: TextView = view.findViewById(R.id.pd_description)
        placeDescription.text = description

        val placeRating: TextView = view.findViewById(R.id.pd_rating)
        placeRating.text = rating

        // Setting the stuff for the reviews listview
        val reviewsListView = view.findViewById<ListView>(R.id.pd_reviews)

        //Continuar aqui

        //val dataList = listOf("Test", "Test2", "Test3","Test", "Test2", "Test3")
        val reviewsAdapter = CustomAdapter(requireContext(), dataReviews)
        reviewsListView.adapter = reviewsAdapter

        val buttonMap: Button = view.findViewById(R.id.buttonMap)
        buttonMap.setOnClickListener{
            val bundle = Bundle()
            bundle.putString("title",title)
            latitude?.let { it1 -> bundle.putDouble("latitude", it1) }
            longitude?.let { it1 -> bundle.putDouble("longitude", it1) }
            val fragment = OSMFragment()
            fragment.arguments = bundle
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}

class CustomAdapter(context: Context, private val dataList: List<Review>) :
    ArrayAdapter<Review>(context, 0, dataList) {
    val db = Firebase.firestore
    val user = Firebase.auth
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.place_details_review, parent, false)
        }
        val textView = itemView!!.findViewById<TextView>(R.id.pd_review_username)
        val textView1 = itemView.findViewById<TextView>(R.id.pd_user_review)
        val imageV = itemView.findViewById<ImageView>(R.id.pd_user_img)
        val userRat = itemView.findViewById<RatingBar>(R.id.pd_user_rating)
        var imgLink : String = "";
        db.collection("users").whereEqualTo("id",dataList[position].userID).get().addOnSuccessListener {
            task ->
            for(document in task){
                imgLink = document.data.get("profileURL").toString()
                Glide.with(itemView!!)
                    .load(imgLink)
                    .into(imageV!!)
            }
        }
        if(dataList[position].userName.length > 16) {textView?.text = dataList[position].userName.substring(0,16)}
        else  {textView?.text = dataList[position].userName}
        textView1?.text = dataList[position].description
        userRat.rating = dataList[position].rating.toFloat()

        return itemView
    }
}