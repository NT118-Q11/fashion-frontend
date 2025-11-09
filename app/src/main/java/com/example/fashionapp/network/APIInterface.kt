package com.example.fashionapp.network

import com.example.fashionapp.product.Product
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/products")
    fun getProducts(): Call<List<Product>>
}
