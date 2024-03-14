package com.example.descompuestos

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
import androidx.fragment.app.Fragment
import com.example.descompuestos.databinding.ActivityMainBinding
import org.osmdroid.views.MapView
import java.io.File

class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG: String = "dESCOMpuestos"
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private var coordsInfo: String = "Not set yet"
    private val locationPermissionCode = 2
    private var latestLocation: Location? = null

    private fun askForUserId(){

        val input = EditText(this);
        var test: String ="";
        this.let {
            AlertDialog.Builder(it)
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

                    } else Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show();
                }.setNegativeButton("Cancel",null).show()
        }
    }

    private fun saveUserID(userID : String){
        val sharedPreferences =
            this.getSharedPreferences("AppPreferences",Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            sharedPreferences.edit().apply{
                putString("userID",userID)
                apply()
            }
        }
    }

    private fun getUserID():String?{
        val sharedPreferences =
            this.getSharedPreferences("AppPreferences",Context.MODE_PRIVATE);
        return if (sharedPreferences != null) {
            sharedPreferences.getString("userID",null)
        };
        else "Shared preferences is empty"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: The main activity is being created")


        val userID = getUserID();
        if(getUserID()== null) askForUserId()
        else Toast.makeText(this,"UserID: $userID", Toast.LENGTH_LONG).show()

        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if(this.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED && this.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED){
            this.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    locationPermissionCode
                )
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f,this);
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MainFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId){
                R.id.nav_home -> replaceFragment(MainFragment())
                R.id.nav_map -> replaceFragment(OSMFragment())
                R.id.nav_visited -> replaceFragment(VisitedFragment())
                R.id.nav_profile -> replaceFragment(VisitedFragment())
                else ->{

                }
            }
            true
        }
    }

    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrameLayout, fragment)
        fragmentTransaction.commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (let { ActivityCompat.checkSelfPermission(it.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }
        }
    }
    override fun onLocationChanged(location: Location) {
        //val textView: TextView = findViewById(R.id.tvInfo)
        this.latestLocation = location
        Toast.makeText(this, "New coordinates: [${location.latitude},${location.longitude}]", Toast.LENGTH_LONG).show()
        //textView.text = "Latitude: [${location.latitude}]\nLongitude: [${location.longitude}]\nUserId: [${getUserID()}]"
        saveCoordinatesToFile(location.latitude, location.longitude)
        coordsInfo = "Latitude: [${location.latitude}]\nLongitude: [${location.longitude}]\nUserId: [${getUserID()}]"
    }

    private fun saveCoordinatesToFile(latitude: Double, longitude: Double) {
        val fileName = "gps_coordinates.csv"
        val file = File(filesDir, fileName)
        val timestamp = System.currentTimeMillis()
        file.appendText("$timestamp;$latitude;$longitude\n")
    }

    fun getMyData(): String {
        return coordsInfo
    }

    fun getLatestLocation(): Location? {
        return latestLocation
    }
}


