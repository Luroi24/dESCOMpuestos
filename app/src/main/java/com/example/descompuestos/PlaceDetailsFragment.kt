package com.example.descompuestos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class PlaceDetails : Fragment() {
    var placeId: Int? = null
    var title: String? = ""
    lateinit var placeImg: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val data = arguments
        placeId = data?.getInt("id")
        title = data?.getString("title")
        var aux = data?.getStringArrayList("imgUrls")
        if (aux != null) {
            placeImg = aux.toList()
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

        // Setting the stuff for the reviews listview
        val reviewsListView = view.findViewById<ListView>(R.id.pd_reviews)
        val dataList = listOf("Test", "Test2", "Test3","Test", "Test2", "Test3")
        val reviewsAdapter = CustomAdapter(requireContext(), dataList)
        reviewsListView.adapter = reviewsAdapter
    }
}

class CustomAdapter(context: Context, private val dataList: List<String>) :
    ArrayAdapter<String>(context, 0, dataList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var itemView = convertView
        if (itemView == null) {
            itemView = LayoutInflater.from(context).inflate(R.layout.place_details_review, parent, false)
        }
        val textView = itemView!!.findViewById<TextView>(R.id.pd_review_username)
        textView.text = dataList[position]
        return itemView
    }
}