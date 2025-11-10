package com.example.fashionapp.uix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.Product
import com.example.fashionapp.adapter.Adapter
import com.example.fashionapp.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home)

        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
        recycler.layoutManager = GridLayoutManager(this, 2)

        val products = listOf(
            Product(id = 1, name = "Áo thun trắng", price = 199000, imageRes = R.drawable.shirt_white, description = "..."),
            Product(id = 2, name = "Váy hoa mùa hè", price = 299000, imageRes = R.drawable.dress_flower, description = "..."),
            Product(id = 3, name = "Giày sneaker", price = 499000, imageRes = R.drawable.sneaker, description = "...")
        )
        recycler.adapter = Adapter(products) { product ->
            val intent = Intent(this@Home, Detail::class.java)
            intent.putExtra("id", product.id)
            intent.putExtra("name", product.name)
            intent.putExtra("price", product.price)
            intent.putExtra("image", product.imageRes)
            intent.putExtra("desc", product.description)
            startActivity(intent)
        }
    }
}