package com.example.fashionapp.product

data class ShippingAddress(
    val id: Int,
    val name: String,
    val address: String,
    val phone: String,
    var isSelected: Boolean = false
)


