package com.example.fashionapp.uix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.Product
import com.example.fashionapp.adapter.Adapter

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.xml.home)

        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val products = listOf(
            Product(1, "Áo thun trắng", 199000.0, R.drawable.shirt_white, "Áo thun cotton thoáng mát"),
            Product(2, "Váy hoa mùa hè", 299000.0, R.drawable.dress_flower, "Váy hoa chất vải nhẹ, phù hợp đi chơi"),
            Product(3, "Giày sneaker", 499000.0, R.drawable.sneaker, "Sneaker unisex, năng động"),
        )

        recycler.adapter = Adapter(products) { product ->
            // TODO: chuyển sang màn hình chi tiết
        }
    }
}