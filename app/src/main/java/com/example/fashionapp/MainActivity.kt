package com.example.fashionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.databinding.ActivityMainBinding
import com.example.fashionapp.databinding.DetailsBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding // THAY ĐỔI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout của Activity và thiết lập content view
        binding = ActivityMainBinding.inflate(layoutInflater) // THAY ĐỔI
        setContentView(binding.root)

    }
}
