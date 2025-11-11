package com.example.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.uix.Home

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Dòng này sẽ liên kết file layout welcome.xml với Activity này
        setContentView(R.layout.settings)
    }
}

