package com.example.descompuestos

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Firebase.firestore;
        setContentView(R.layout.activity_main2)
        val buttonSend: Button = findViewById(R.id.buttonLoginSend)
        buttonSend.setOnClickListener{
            var userName : String;
            var userPass : String;
        var editTextName = findViewById(R.id.editTextText) as EditText
        var editTextPass = findViewById(R.id.editTextTextPassword) as EditText
        userName = editTextName.text.toString();
        userPass = editTextPass.text.toString();
            if(userName.isNotBlank() && userPass.isNotBlank()){
                db.collection("users")
                    .whereEqualTo("email", userName)
                    .get()
                    .addOnSuccessListener { result ->
                        if (!result.isEmpty){
                                    //Toast.makeText(this,"Este correo existe",Toast.LENGTH_LONG).show();
                                    val document = result.documents.get(0);
                                    val retreivedPass = document.data?.getValue("password");
                                    if (userPass == retreivedPass) {
                                        Toast.makeText(
                                            this,
                                            "Correo y contraseña correctos",
                                            Toast.LENGTH_LONG
                                        ).show();
                                    } else {
                                        Toast.makeText(this, "Incorrect Password", Toast.LENGTH_LONG).show();
                                    }
                        }
                        else{
                            Toast.makeText(this, "Invalid email", Toast.LENGTH_LONG)
                                .show();
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error retrieving data", exception)
                    }
                        Toast.makeText(this,"User Name: $userName, User Pass: $userPass",Toast.LENGTH_LONG).show();

         }
        }
    }
}