package com.example.descompuestos

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.fontResource
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val db = Firebase.firestore
        val user = Firebase.auth
        val userName = user.currentUser?.displayName
        val userEmail = user.currentUser?.email
        val userID = user.currentUser?.uid
        var userImage : String = ""
        var userLocation :String =""


        var finalName : String = "";
        var finalLastName : String ="";
        var counter : Int = 0;
        userName!!.forEach{ it->
            if(counter < 2){
            finalName += it
            }
            else{
                finalLastName += it;
            }
            if(it == ' ') counter++;
        }

        super.onViewCreated(view, savedInstanceState)

        val usernameTV = view.findViewById<TextView>(R.id.firstNameET)
        val lastNameTV = view.findViewById<TextView>(R.id.lastNameET)
        val emailTV = view.findViewById<TextView>(R.id.emailET)
        val countryTV = view.findViewById<TextView>(R.id.countryET)
        val imgTV = view.findViewById<ImageView>(R.id.userIMG)

        db.collection("users").whereEqualTo("id",userID).get().addOnSuccessListener {
                it ->
            userImage = it.documents[0].get("profileURL").toString()
            userLocation = it.documents[0].get("location").toString()

            Glide.with(requireView())
                .load(userImage)
                .into(imgTV!!)

            countryTV.text = userLocation
        }

        usernameTV.text = finalName
        lastNameTV.text = finalLastName
        emailTV.text = userEmail




    }

    private fun getUserID():String?{
        val sharedPreferences =
            activity?.getSharedPreferences("AppPreferences", AppCompatActivity.MODE_PRIVATE);
        return if (sharedPreferences != null) {
            sharedPreferences.getString("userID",null)
        };
        else "Shared preferences is empty"
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}