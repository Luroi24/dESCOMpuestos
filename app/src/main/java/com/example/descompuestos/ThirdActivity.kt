package com.example.descompuestos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button

class ThirdActivity : AppCompatActivity() {
    private val TAG: String = "ThirdActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)
        Log.w(TAG, "onCreate: Change variable name \"TAG\". For good practices it should start with lowercase letters")

        val buttonPrev: Button = findViewById(R.id.buttonPrev2)
        buttonPrev.setOnClickListener{
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }
}