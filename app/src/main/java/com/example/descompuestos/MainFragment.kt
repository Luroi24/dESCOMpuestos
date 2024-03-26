package com.example.descompuestos

import MyAdapter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class MainFragment : Fragment() {


    //var dataList = arrayListOf<String>();
    //var imgUrls = arrayListOf<String>()

    var itemsTest = arrayListOf<Store>()



    /*
    val dataList = listOf("Tornasol",
        "Toma Café",
        "Religion Coffee",
        "Plántate",
        "Misión Café",
        "Hanso Café",
        "Casa Neutrale",
        "Cafelito",
        "Café Angélica",
        "Nolita Coffee & Brunch")
    val imgUrls = listOf("https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-tornasol-65099dd45b3c2.jpeg?crop=0.670xw:1.00xh;0.330xw,0&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-toma-cafe-2-65099dd44362f.jpeg?crop=0.75xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-religion-65099dd44ccbc.jpeg?crop=0.9833333333333333xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-plantate-65099dd472bd4.jpeg?crop=0.6666666666666666xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-mision-65099dd3781eb.jpeg?crop=0.9243055555555556xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-hanso-65099dd34cd59.jpeg?crop=1xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-casa-neutrale-65099dd35e294.jpeg?crop=1.00xw:0.801xh;0,0.152xh&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-cafelito-65099dd360a27.jpeg?crop=0.69xw:1xh;center,top&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-cafe-angelica-65099dd38d6be.jpeg?crop=1.00xw:0.802xh;0,0.134xh&resize=980:*",
        "https://hips.hearstapps.com/hmg-prod/images/mejores-cafeterias-madrid-anolita-65099dd37c57b.png?crop=1xw:1xh;center,top&resize=980:*")

     */

    private fun setData(){
        //var imgUrls = arrayListOf<String>();
        var x : List<String>? = null;
        var y : List<String>? = null;
        var testing : String = "";
        val preferences : SharedPreferences? = this.activity?.getSharedPreferences("AppData",0);
        if(preferences != null){
            testing = preferences.getString("test",null).toString()
        }
        var gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Store?>?>() {}.getType()
        val pruebaEnFragmento : ArrayList<Store> = gson.fromJson(testing,type)
        itemsTest = pruebaEnFragmento;
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        setData()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_test, container, false)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity: MainActivity = activity as MainActivity
        val myDataFromActivity: String = activity.getMyData()
        val textView: TextView = view.findViewById(R.id.tvInfo)
        textView.text = myDataFromActivity

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = MyAdapter(itemsTest, R.layout.main_recycler_view)
        adapterOnClickListener(adapter)
        recyclerView.adapter = adapter

        val nearbyRecyclerView: RecyclerView = view.findViewById(R.id.nearby_recycler_view)
        nearbyRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val nearbyAdapter = MyAdapter(itemsTest, R.layout.main_recycler_viewer_nearby)
        adapterOnClickListener(nearbyAdapter)
        nearbyRecyclerView.adapter = nearbyAdapter
    }

    fun adapterOnClickListener(adapter: MyAdapter){
        adapter.setOnItemClickListener(
            object : MyAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    val bundle = Bundle()
                    bundle.putInt("id",position)
                    bundle.putString("title", itemsTest[position].storeName)
                    bundle.putStringArrayList("imgUrls", ArrayList(itemsTest[position].imageLinks))
                    bundle.putString("description", itemsTest[position].description)
                    bundle.putString("rating",itemsTest[position].ratingPlace.toString())
                    val fragment = PlaceDetails()
                    fragment.arguments = bundle
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.mainFrameLayout, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                }
            })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MainFragment()
                .apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}