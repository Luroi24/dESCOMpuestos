package com.example.descompuestos

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import java.util.HashMap
import com.google.firebase.firestore.AggregateField
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ServerTimestamp
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.firestore.persistentCacheSettings
import com.google.firebase.firestore.toObject
import com.google.firebase.Firebase

class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG: String = "dESCOMpuestos"
    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    private var latestLocation: Location? = null

    private fun askForUserId(){
        val input = EditText(this);
        var test: String ="";
        AlertDialog.Builder(this)
            .setTitle("Enter User Id")
            .setIcon(R.mipmap.ic_launcher)
            .setView(input)
            .setPositiveButton("Save") { dialog, which ->
                val userInput =  input.text.toString()
                if (userInput.isNotBlank()){
                    saveUserID(userInput)
                    Toast.makeText(
                    this,
                    "UserID saved: $userInput",
                    Toast.LENGTH_LONG

                ).show();

                }
                else Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show();
            }.setNegativeButton("Cancel",null).show()
    }

    private fun saveUserID(userID : String){
        val sharedPreferences = this.getSharedPreferences("AppPreferences",Context.MODE_PRIVATE);
        sharedPreferences.edit().apply{
            putString("userID",userID)
            apply()
        }
    }

    private fun getUserID():String?{
        val sharedPreferences = this.getSharedPreferences("AppPreferences",Context.MODE_PRIVATE);
        return sharedPreferences.getString("userID",null);
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        val userID = getUserID();
        /*
        val db = Firebase.firestore;
        val user = hashMapOf(
            "email" to "ada@gmail.com",
            "name" to "Juan Perez",
            "password" to "juanito",
        )
        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }

         */
        if(getUserID()== null) askForUserId()
        else Toast.makeText(this,"UserID: $userID",Toast.LENGTH_LONG).show()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f,this);
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: The main activity is being created")

        val buttonNext: Button = findViewById(R.id.button)
        buttonNext.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        buttonLogin.setOnClickListener{
            val intent = Intent(this, MainActivity2::class.java)
            startActivity(intent)
        }

        val osmButton: Button = findViewById(R.id.buttonOSM)
        osmButton.setOnClickListener{
           if (latestLocation != null){
                val intent = Intent(this, OSMActivity::class.java)
                val bundle = Bundle()
                bundle.putParcelable("location", latestLocation)
                intent.putExtra("locationBundle", bundle)
                startActivity(intent)
           }else{ Log.e(TAG, "Location not set yet")}
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }
        }
    }
    override fun onLocationChanged(location: Location) {
        val textView: TextView = findViewById(R.id.mainTextView)
        this.latestLocation = location
        Toast.makeText(this, "New coordinates: [${location.latitude},${location.longitude}]", Toast.LENGTH_LONG).show()
        textView.text = "Latitude: ${location.latitude}\nLongitude: ${location.longitude}"
    }
}


