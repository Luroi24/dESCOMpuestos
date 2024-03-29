package com.example.descompuestos

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOError
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SecondActivity : AppCompatActivity() {
    @SuppressLint("CutPasteId", "MissingInflatedId")
    private val TAG: String = "SecondActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val buttonNext: Button = findViewById(R.id.buttonNext)
        buttonNext.setOnClickListener{
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }
        val buttonPrev: Button = findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // Setting up the listView
        val listView: ListView = findViewById(R.id.listOfCoords)
        val headerView = layoutInflater.inflate(R.layout.listview_header, listView, false)
        listView.addHeaderView(headerView, null, false)

        val adapter = CoordinatesAdapter(this, readFileContents())
        listView.adapter = adapter

        // bottom nav bar
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav)
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_map -> {
                    val intent = Intent(this, OSMActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_visited -> {
                    val intent = Intent(this, SecondActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private class CoordinatesAdapter(context: Context, private val coordinatesList: List<List<String>>): ArrayAdapter<List<String>>(context, R.layout.listview_item, coordinatesList){
        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View{
            val view = convertView ?: inflater.inflate(R.layout.listview_item, parent, false)

            val timestampTextView: TextView = view.findViewById(R.id.tvTimestamp)
            val latitudeTextView: TextView = view.findViewById(R.id.tvLatitude)
            val longitudeTextView: TextView = view.findViewById(R.id.tvLongitude)

            try{
                val item = coordinatesList[position]
                timestampTextView.text = formatTimestamp(item[0].toLong())
                latitudeTextView.text = formatCoordinate(item[1].toDouble())
                longitudeTextView.text = formatCoordinate(item[2].toDouble())

                view.setOnClickListener{
                    val intent = Intent(context, ThirdActivity::class.java).apply{
                        putExtra("latitude", item[1])
                        putExtra("longitude", item[2])
                    }
                    context.startActivity(intent)
                }
            } catch (e: Exception){
                e.printStackTrace()
                Log.e("CoordinatesAdapter", "getView: Exception parsing coordinates.")
            }
            return view
        }

        private fun formatTimestamp(timestamp: Long): String{
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            return formatter.format(Date(timestamp))
        }
        private fun formatCoordinate(value: Double): String {
            return String.format("%.6f", value)
        }
    }

    private fun readFileContents(): List<List<String>>{
        val fileName = "gps_coordinates.csv"
        return try {
            openFileInput(fileName).bufferedReader().useLines { lines ->
                lines.map { it.split(";").map(String::trim) }.toList()
            }
        }catch (e: IOException){
            listOf(listOf("Error reading file: ${e.message}"))
        }
    }
}