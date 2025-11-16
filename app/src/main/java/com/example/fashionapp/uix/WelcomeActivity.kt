package com.example.fashionapp.uix

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.fashionapp.R
import com.google.android.material.button.MaterialButton

class WelcomeActivity : AppCompatActivity() {
<<<<<<< HEAD
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnSignIn = findViewById<MaterialButton>(R.id.button_sign_in)
        val btnRegister = findViewById<MaterialButton>(R.id.button_sign_up)

        btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
=======
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_welcome)
//
//        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
//        val btnRegister = findViewById<Button>(R.id.btnRegister)
//
//        btnSignIn.setOnClickListener {
//            startActivity(Intent(this, SignInActivity::class.java))
//        }
//
//        btnRegister.setOnClickListener {
//            startActivity(Intent(this, RegisterActivity::class.java))
//        }
//    }
>>>>>>> main
}
