package com.example.descompuestos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.descompuestos.R

class StartingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starting)
        val buttonRegister: Button = findViewById(R.id.go_to_register)
        buttonRegister.setOnClickListener{
            val intent = Intent(this, UserAuthenticator::class.java)
            startActivity(intent)
        }
    }
}