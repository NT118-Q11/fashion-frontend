package com.example.fashionapp.product

data class CartItem(
    val id: Int,
    val brand: String,
    val name: String,
    val price: Int,
    val imageRes: Int,
    var quantity: Int = 1
)

