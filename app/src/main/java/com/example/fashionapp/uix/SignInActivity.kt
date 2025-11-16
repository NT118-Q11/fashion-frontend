package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {
<<<<<<< HEAD

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonSignIn.setOnClickListener {
            // sau khi đăng nhập thành công
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // không quay lại login
        }
    }
=======
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_sign_in)
//    }
>>>>>>> main
}
