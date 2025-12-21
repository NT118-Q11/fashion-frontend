package com.example.fashionapp.model

data class ReelItem(
    val id: String,
    val imageAssetPath: String, // e.g., "women/women1.jpg"
    val brand: String,
    val name: String,
    val priceText: String
)
