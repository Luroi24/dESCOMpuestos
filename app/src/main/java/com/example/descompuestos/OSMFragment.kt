package com.example.descompuestos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OSMFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var TAG = "Open Street Map Activity"
    private var map: MapView? = null
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
        return inflater.inflate(R.layout.fragment_o_s_m, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Configuration.getInstance().userAgentValue = "MadridMug"
        val mapView = view.findViewById<MapView>(R.id.mapFragment)
        map = mapView
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        val activity: MainActivity = activity as MainActivity
        val location: Location? = activity.getLatestLocation()
        val startPoint = location?.let { GeoPoint(it.latitude, location.longitude) }
        //val startPoint = GeoPoint(40.416775, -3.703790) in case you want to test it mannualy
        mapView.controller.setCenter(startPoint)
        addMarker(mapView, startPoint, "Current location")
        //addMarkers(map,oneDayInMadrid, oneDayInMadridNames)
        addMarkersAndRoute(mapView,oneDayInMadrid, oneDayInMadridNames)
        val initialLocation = GeoPoint(40.7128, -74.0060) // Coordinates of New York City
        mapView.controller.setCenter(startPoint)
        mapView.controller.setZoom(18.0)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OSMFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun addMarker(mapView: MapView, point: GeoPoint?, title: String){
        val marker = Marker(mapView)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapView.overlays.add(marker)
        mapView.invalidate() // This makes the map reload.
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
        route.color = activity?.let { ContextCompat.getColor(it, R.color.teal_700) }!!
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
        map?.onResume()
    }
    override fun onPause() {
        super.onPause()
        map?.onPause()
    }
}