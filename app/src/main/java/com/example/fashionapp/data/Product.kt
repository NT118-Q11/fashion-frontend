
package com.example.fashionapp.data

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val assetImage: String // filename inside app/src/main/assets/
)
