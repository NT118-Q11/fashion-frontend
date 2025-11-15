package com.example.fashionapp.uix

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.home)
//
//        val recycler = findViewById<RecyclerView>(R.id.recyclerProducts)
//        recycler.layoutManager = GridLayoutManager(this, 2)
//
//        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
//            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
//                if (response.isSuccessful) {
//                    val products = response.body() ?: emptyList()
//                    recycler.adapter = Adapter(products) {
//                        selectedProduct -> Log.d("Home", "Selected product: ${selectedProduct.name}")
//                    }
//                } else {
//                    Log.e("Home", "Response failed: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
//                Log.e("Home", "API call failed: ${t.message}")
//            }
//        })
//    }
}