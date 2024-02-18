package com.example.descompuestos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class OSMActivity : AppCompatActivity() {
    private var TAG = "Open Street Map Activity"
    private lateinit var map: MapView
    private val oneDayInMadrid = listOf(
        GeoPoint(40.41845936702104, -3.69652793926266), // Círculo de Bellas Artes
        GeoPoint(40.420258671427085, -3.705530552913505), // Gran Vía hasta Callao
        GeoPoint(40.416874704683174, -3.703594587176437), // Plaza Sol
        GeoPoint(40.415656434975155, -3.7074665187142464), // Plaza Mayor
        GeoPoint(40.41511982217783, -3.7145655110883804), // Catedral de Santa María la Real de la Almudena
        GeoPoint(40.418414913088995, -3.7131300511180254), // Palacio Real de Madrid
        GeoPoint(40.423468167915836, -3.712169480167424), // Plaza de España
        GeoPoint(40.42428783770509, -3.707340026225939), // Intersection to walk between various restaurants, cafes, bars and clubs
        GeoPoint(40.42221872286782, -3.6977097614807177), // Mercado de San Antón
        GeoPoint(40.41388796506851, -3.6920841884712456), // Museo Nacional del Prado
        GeoPoint(40.417302682265564, -3.683576823935882), // Retiro al atardecer!
    )
    private val oneDayInMadridNames = listOf(
        "Círculo de Bellas Artes",
        "Gran Vía",
        "Puerta del Sol",
        "Plaza Mayor",
        "Catedral de Santa María la Real de la Almudena",
        "Palacio Real de Madrid",
        "Plaza de España",
        "Malasaña",
        "Mercado de San Antón",
        "Museo Nacional del Prado",
        "Retiro"
    )
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
            map.controller.setZoom(18.0)
            val startPoint = GeoPoint(location.latitude, location.longitude)
            //val startPoint = GeoPoint(40.416775, -3.703790) in case you want to test it mannualy
            map.controller.setCenter(startPoint)
            addMarker(startPoint, "Current location")
            //addMarkers(map,oneDayInMadrid, oneDayInMadridNames)
            addMarkersAndRoute(map,oneDayInMadrid, oneDayInMadridNames)
        }else{
            Log.e(TAG, "location: The location hasn't been set.")
        }
    }

    private fun addMarker(point: GeoPoint, title: String){
        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        map.overlays.add(marker)
        map.invalidate() // This makes the map reload.
    }

    private fun addMarkers(mapView: MapView, locationsCoords: List<GeoPoint>, locationsNames: List<String>){
        for(location in locationsCoords){
            val marker = Marker(mapView)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    private fun addMarkersAndRoute(mapView: MapView, locationsCoords: List<GeoPoint>, locationsNames: List<String>){
        if (locationsCoords.size != locationsNames.size){
            Log.e("addMarkersAndRoute", "Locations and names list must have the same number of elements")
            return
        }
        val route = Polyline()
        route.setPoints(locationsCoords)
        route.color = ContextCompat.getColor(this, R.color.teal_700)
        mapView.overlays.add(route)

        // TODO: Refactor this code.

        val icCameraOriginal: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_camera)
        val newWidth = 300
        val newHeight = 300

        val icCameraResized = Bitmap.createScaledBitmap(icCameraOriginal,newWidth,newHeight, false)


        for (location in locationsCoords){
            val marker = Marker(mapView)
            marker.position = location
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            val locationIndex = locationsCoords.indexOf(location)
            marker.title = "Marker at ${locationsNames[locationIndex]} ${location.latitude}, ${location.longitude}"
            marker.icon = BitmapDrawable(resources, icCameraResized)
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
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