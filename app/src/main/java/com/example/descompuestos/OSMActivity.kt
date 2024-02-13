package com.example.descompuestos

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

class OSMActivity : AppCompatActivity() {
    private var TAG = "Open Street Map Activity"
    private lateinit var map: MapView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_osm)
        val bundle = intent.getBundleExtra("locationBundle")
        val location: Location? = bundle?.getParcelable("location")

        Log.d(TAG, "onCreate: The osmActivity is being created")

        if (location != null) {
            Log.i(TAG, "onCreate: Location["+location.altitude+"]["+location.latitude+"]["+location.longitude+"][")
            Configuration.getInstance().load(applicationContext, getSharedPreferences("osm", MODE_PRIVATE))
            map = findViewById<MapView>(R.id.map)
            map.setTileSource(TileSourceFactory.MAPNIK)
            map.controller.setZoom(15.0)
            val startPoint = GeoPoint(location.latitude, location.longitude)
            //val startPoint = GeoPoint(40.416775, -3.703790) in case you want to test it mannualy
            map.controller.setCenter(startPoint)
        };
    }
    override fun onResume() {
        super.onResume()
        map.onResume()
    }
    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}