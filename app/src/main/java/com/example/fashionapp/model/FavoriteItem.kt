package com.example.fashionapp.model

data class FavoriteItem(
    val id: String,
    val name: String,
    val desc: String,
    val price: String,
    val imageRes: Int = 0,
    val imagePath: String = ""
)