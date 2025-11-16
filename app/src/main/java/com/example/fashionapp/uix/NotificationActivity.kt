package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivityNotificationBinding

class NotificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavBar()
    }

    private fun setupNavBar() {

        // HOME -> HomeActivity
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish() // đóng NotificationActivity
        }

        // Notification -> đang ở đây nên KHÔNG cần gì
        binding.navNotification.setOnClickListener {
            // Không làm gì vì đang ở màn này
        }

        // Cart
        binding.navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // Account
        binding.navAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}
