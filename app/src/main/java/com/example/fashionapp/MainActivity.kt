package com.example.fashionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.DetailsBinding
import com.google.android.material.tabs.TabLayoutMediator // 1. Import lớp này

class MainActivity : AppCompatActivity() {

    private lateinit var binding: DetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageList = listOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        val adapter = ImageSliderAdapter(imageList)
        binding.viewPagerMain.adapter = adapter


        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { tab, position ->

        }.attach()
    }
}
