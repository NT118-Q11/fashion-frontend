package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivityMyFavoritesBinding
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.adapter.FavoritesAdapter
import com.example.fashionapp.R

class MyFavoritesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyFavoritesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyFavoritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fake data
        val items = listOf(
            FavoriteItem("LAMEREI", "reversible angora cardigan", "$120", R.drawable.sample_woman),
            FavoriteItem("FENDI", "cotton jacket", "$210", R.drawable.sample_woman),
            FavoriteItem("CHANEL", "classic coat", "$350", R.drawable.sample_woman),
        )

        binding.rvFavorites.adapter = FavoritesAdapter(items)

        // Quay lại
        binding.ivBack.setOnClickListener { finish() }

        // ⭐ Chuyển sang Home
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        // Chuyển sang Notification
        binding.navNotification.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }
}
