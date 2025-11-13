package com.example.fashionapp.product

data class DeliveryMethod(
    val id: Int,
    val name: String,
    val price: Int,
    val description: String,
    var isSelected: Boolean = false
)


