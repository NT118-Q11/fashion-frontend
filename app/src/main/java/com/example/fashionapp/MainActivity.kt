package com.example.fashionapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Dòng này sẽ "thổi phồng" layout activity_main.xml
        binding = ActivityMainBinding.inflate(layoutInflater)

        // Dòng này thiết lập view cho Activity.
        // Sau bước này, FragmentContainerView sẽ được tạo ra và
        // tự động hiển thị Fragment khởi đầu (start destination) từ nav_graph.
        setContentView(binding.root)
    }
}
