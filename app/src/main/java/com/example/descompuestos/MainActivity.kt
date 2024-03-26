package com.example.descompuestos

import android.Manifest
import android.content.pm.PackageManager
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
import com.example.descompuestos.databinding.ActivityMainBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File
import java.lang.reflect.Type


class Store(
    val storeName : String,
    val imageLinks : List<String>,
    val idPlace : Long,
    val ratingPlace : Double,
    val coordinates: Pair<Double,Double>,
    val description: String
){ }

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

    private fun retrieveData(){
        var dataList = arrayListOf<String>();
        var imgUrls = arrayListOf<String>();

        var string : String = "";

        var storeName : String? = null
        var imageLinks : List<String>? = null
        var idPlace : Long? = null
        var ratingPlace : Double? = null
        var coordinates: Pair<Double,Double>? = null
        var description: String? = null;


        var gson = Gson()
        val type: Type = object : TypeToken<ArrayList<Store?>?>() {}.getType()

        val prueba = mutableListOf<Store>()

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


                Log.i("test", "storeName: $storeName");
                Log.i("test", "imageLinks: $imageLinks");
                Log.i("test", "idPlace: $idPlace");
                Log.i("test", "coordinates: $coordinates");
                Log.i("test", "ratingPlace: $ratingPlace");
                Log.i("test","description: $description")

                var item = Store(storeName!!,imageLinks!!,idPlace!!,ratingPlace!!,coordinates!!,description!!)
                prueba.add(item)
            }

            string = gson.toJson(prueba,type)

            Log.i("finalTest",string)

        }

       while(string.length == 0) {
       }

        val sharedPreferences = this.getSharedPreferences("AppData", MODE_PRIVATE);
        if(sharedPreferences != null){
            sharedPreferences.edit().apply{
                putString("test",string)
                apply()
            }
        }

    }


    private fun printData(){
        var x : String = "";
        var y : String = "";
        val sharedPreferences = this.getSharedPreferences("AppData", MODE_PRIVATE);
        if(sharedPreferences != null){
             x = sharedPreferences.getString("dataList",null) as String;
             y = sharedPreferences.getString("imageURLs",null).toString();
        }
        //Log.i("AppDataLog",x);
        //Log.i("AppDataLog",y);
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


    override fun onCreate(savedInstanceState: Bundle?) {

        retrieveData()
        printData()

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


