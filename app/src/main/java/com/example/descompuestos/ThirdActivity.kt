package com.example.descompuestos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        val TAG: String = "ThirdActivity"
        Log.d(TAG, "onCreate: The third activity is being created")

        val buttonPrev: Button = findViewById(R.id.buttonPrev2)
        buttonPrev.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}