package com.example.descompuestos

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.reflect.Type

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WriteRatingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WriteRatingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var title: String? = ""
    private var idPlace: Int? = null

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
        val data = arguments
        title = data?.getString("cafeteriaTitle")
        idPlace = data?.getInt("id")
        return inflater.inflate(R.layout.fragment_write_rating, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var titleText: TextView = view.findViewById<TextView>(R.id.review_place_title)
        titleText.text = title
        val user = Firebase.auth
        val db = Firebase.firestore

        val buttonPrev: Button = view.findViewById(R.id.send_shiit)
        buttonPrev.setOnClickListener{
            var temp = view.findViewById(R.id.write_user_review) as EditText
            var temp2 = view.findViewById<RatingBar>(R.id.review_rating)
            var description : String = temp.text.toString();
            var ratVal : Double? = temp2.rating.toDouble()
            var userName = user.currentUser?.displayName.toString()
            var userID = user.currentUser?.uid.toString();

            val reviewComplete = hashMapOf(
                "description" to description,
                "placeID" to idPlace?.toInt(),
                "rating" to ratVal,
                "userName" to userName,
                "userID" to userID
            )

            db.collection("reviews").document().set(reviewComplete).addOnSuccessListener {

                val rev = Firebase.firestore.collection("reviews")
                var gson1 = Gson()
                val type1: Type = object : TypeToken<ArrayList<Review?>?>() {}.getType()
                var string1 : String = "";
                val reviewsArray = mutableListOf<Review>()

                var descRev: String? = null
                var idPlaceRev : Long? = null
                var userIDRev : String? = null
                var ratingRev: Double? = null
                var userName : String? = null;

                GlobalScope.launch(Dispatchers.IO){
                    var documents = rev.get().await()
                    documents.forEach{
                            it1->
                        descRev = it1.data.get("description").toString();
                        idPlaceRev = it1.data.get("placeID") as Long;
                        userIDRev = it1.data.get("userID") as String;
                        ratingRev = it1.data.get("rating") as Double;
                        userName = it1.data.get("userName") as String;


                        var item1 = Review(descRev!!,idPlaceRev!!,userName!!,userIDRev!!,ratingRev!!)
                        reviewsArray.add(item1)
                    }

                    string1 = gson1.toJson(reviewsArray,type1)

                    Log.i("reviewsFinal",string1)

                }

                while(string1.length==0) { }


                val sharedPreferences = activity?.getSharedPreferences("AppData",
                    AppCompatActivity.MODE_PRIVATE
                );
                if(sharedPreferences != null){
                    sharedPreferences.edit().apply{
                        putString("reviews",string1)
                        apply()
                    }
                }


                returnToMain()
            }

        }


    }




    fun returnToMain(){
        val bundle = Bundle()
        val fragment = MainFragment()
        fragment.arguments = bundle
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WriteRatingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WriteRatingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}