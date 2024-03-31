package com.example.descompuestos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.example.descompuestos.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.reflect.Type
import java.util.Locale


class Store(
    val storeName : String,
    val imageLinks : List<String>,
    val idPlace : Long,
    val ratingPlace : Double,
    val coordinates: Pair<Double,Double>,
    val description: String,
    val category: String
){ }

class Review(
    val description: String,
    val idPlace : Long,
    val userName : String,
    val userID : String,
    val rating: Double
){ }



class MainActivity : AppCompatActivity(), LocationListener {
    private val TAG: String = "dESCOMpuestos"
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationManager: LocationManager
    private var coordsInfo: String = "Not set yet"
    private val locationPermissionCode = 2
    private var latestLocation: Location? = null
    lateinit var database: MMDatabase

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
                        /*Toast.makeText(
                            this,
                            "UserID saved: $userInput",
                            Toast.LENGTH_LONG

                        ).show(); */

                    } else Toast.makeText(this, "User ID cannot be blank", Toast.LENGTH_LONG).show();
                }.setNegativeButton("Cancel",null).show()
        }
    }

    private fun retrieveData(){
        var dataList = arrayListOf<String>();
        var imgUrls = arrayListOf<String>();

        var string : String = "";
        var string1 : String = "";

        var storeName : String? = null
        var imageLinks : List<String>? = null
        var idPlace : Long? = null
        var ratingPlace : Double? = null
        var coordinates: Pair<Double,Double>? = null
        var description: String? = null;
        var cate: String? = null;

        var descRev: String? = null
        var idPlaceRev : Long? = null
        var userIDRev : String? = null
        var ratingRev: Double? = null
        var userName : String? = null;

        var gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Store?>?>() {}.getType()

        var gson1 = Gson()
        val type1: Type = object : TypeToken<ArrayList<Review?>?>() {}.getType()

        val prueba = mutableListOf<Store>()
        val reviewsArray = mutableListOf<Review>()

        val db = com.google.firebase.ktx.Firebase.firestore.collection("places")
        GlobalScope.launch(Dispatchers.IO){
            var documents = db.get().await()
            documents.forEach{
                    it->
                storeName = it.data.get("name").toString();                     //Get Name
                imageLinks = it.data.get("imgURLs") as List<String>;        //Get Images
                idPlace = it.data.get("id") as Long;                        //Get id
                var temp = it.data.get("location") as List<Double>
                var pairTemp = Pair(temp.get(0),temp.get(1))
                coordinates = pairTemp
                ratingPlace = it.data.get("rating") as Double
                description = it.data.get("description").toString()
                cate = it.data.get("category").toString()


                Log.i("test", "storeName: $storeName");
                Log.i("test", "imageLinks: $imageLinks");
                Log.i("test", "idPlace: $idPlace");
                Log.i("test", "coordinates: $coordinates");
                Log.i("test", "ratingPlace: $ratingPlace");
                Log.i("test","description: $description")
                Log.i("test","category: $cate")

                var item = Store(storeName!!,imageLinks!!,idPlace!!,ratingPlace!!,coordinates!!,description!!,cate!!)
                prueba.add(item)
            }

            string = gson.toJson(prueba,type)

            Log.i("finalTest",string)

        }

        val rev = com.google.firebase.ktx.Firebase.firestore.collection("reviews")

        GlobalScope.launch(Dispatchers.IO){
            var documents = rev.get().await()
            documents.forEach{
                    it1->
                descRev = it1.data.get("description").toString();
                idPlaceRev = it1.data.get("placeID") as Long;
                userIDRev = it1.data.get("userID") as String;
                ratingRev = it1.data.get("rating") as Double;
                userName = it1.data.get("userName") as String;

                Log.i("revTest", "descRev: $descRev");
                Log.i("revTest", "idPlaceRev: $idPlaceRev");
                Log.i("revTest", "userIDRev: $userIDRev");
                Log.i("revTest", "ratingRev: $ratingRev");

                var item1 = Review(descRev!!,idPlaceRev!!,userName!!,userIDRev!!,ratingRev!!)
                reviewsArray.add(item1)
            }

            string1 = gson1.toJson(reviewsArray,type1)

            Log.i("reviewsFinal",string1)

        }

       while(string.length == 0 || string1.length==0) {
       }

        val sharedPreferences = this.getSharedPreferences("AppData", MODE_PRIVATE);
        if(sharedPreferences != null){
            sharedPreferences.edit().apply{
                putString("test",string)
                apply()
            }
            sharedPreferences.edit().apply{
                putString("reviews",string1)
                apply()
            }
        }

    }

    private fun saveUserID(userID : String){
        val sharedPreferences =
            this.getSharedPreferences("AppPreferences", MODE_PRIVATE);
        if (sharedPreferences != null) {
            sharedPreferences.edit().apply{
                putString("userID",userID)
                apply()
            }
        }
    }

    private fun getUserID():String?{
        val sharedPreferences =
            this.getSharedPreferences("AppPreferences", MODE_PRIVATE);
        return if (sharedPreferences != null) {
            sharedPreferences.getString("userID",null)
        };
        else "Shared preferences is empty"
    }


    fun authUsers(){
        val user = Firebase.auth
        if(user.currentUser == null){
            val intent = Intent(this, UserAuthenticator::class.java)
            startActivity(intent)
            val db = Firebase.firestore
            val userD = hashMapOf(
                "location" to "Not set",
                "id" to user.currentUser?.uid.toString(),
                "profileURL" to "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2c/Default_pfp.svg/2048px-Default_pfp.svg.png"
            )

            db.collection("users").document().set(userD)

        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        retrieveData()
        authUsers()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: The main activity is being created")


        val userID = getUserID();
        if(getUserID()== null) askForUserId()
        else Toast.makeText(this,"UserID: $userID", Toast.LENGTH_LONG).show()

        locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager
        if(this.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED && this.let {
                ActivityCompat.checkSelfPermission(
                    it.applicationContext,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
            } != PackageManager.PERMISSION_GRANTED){
            this.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                    locationPermissionCode
                )
            }
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5f,this);
        }

        database = Room.databaseBuilder(applicationContext, MMDatabase::class.java, "coordinates").build()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(MainFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId){
                R.id.nav_home -> replaceFragment(MainFragment())
                R.id.nav_map -> replaceFragment(OSMFragment())
                R.id.nav_visited -> replaceFragment(VisitedFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
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
                if (let { ActivityCompat.checkSelfPermission(it.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) } == PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            }
        }
    }
    override fun onLocationChanged(location: Location) {
        //val textView: TextView = findViewById(R.id.tvInfo)
        this.latestLocation = location
        //Toast.makeText(this, "Location: ${getLocalityName(this,location.latitude, location.longitude)}", Toast.LENGTH_LONG).show()
        //textView.text = "Latitude: [${location.latitude}]\nLongitude: [${location.longitude}]\nUserId: [${getUserID()}]"
        saveCoordinatesToFile(location.latitude, location.longitude)
        val newLocation = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = System.currentTimeMillis()
        )
        lifecycleScope.launch(Dispatchers.IO){
            database.locationDao().insertLocation(newLocation)
        }
        coordsInfo = "Latitude: [${location.latitude}]\nLongitude: [${location.longitude}]\nUserId: [${getUserID()}]"
        getLocalityName(this, location.latitude, location.longitude)
    }

    fun getLocalityName(context: Context, latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude,longitude,1)

        return if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val addressComponents = address.getAddressLine(0).split(", ")
            if (addressComponents.size >= 3) {
                Log.i(TAG,addressComponents[0])
                coordsInfo = addressComponents[0]
                coordsInfo
            } else {
                "District not found"
            }
        } else {
            "District not found"
        }
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


