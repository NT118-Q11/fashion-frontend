package com.example.fashionapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.uix.Home

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Có thể dùng layout splash riêng, hoặc chuyển thẳng sang Home
         setContentView(R.layout.home)

        // Chuyển sang HomeActivity ngay sau khi mở app
        val intent = Intent(this, Home::class.java)
        startActivity(intent)
        finish() // Đóng MainActivity để không quay lại khi bấm nút Back
    }
}

