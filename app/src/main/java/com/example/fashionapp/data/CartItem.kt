package com.example.fashionapp.data

data class CartItem(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    var quantity: Int = 1,
    val imageRes: Int
)
