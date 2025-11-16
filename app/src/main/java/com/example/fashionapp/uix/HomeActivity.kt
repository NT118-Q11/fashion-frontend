package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavBar()
        setupTopIcons()
    }

    private fun setupTopIcons() {
        binding.icFavTop.setOnClickListener {
            startActivity(Intent(this, MyFavoritesActivity::class.java))
        }

        binding.icSearch.setOnClickListener {
            // search UI sau này
        }
    }

    private fun setupNavBar() {
        binding.navHome.setOnClickListener {}

        binding.navNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        binding.navCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        binding.navAccount.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}
