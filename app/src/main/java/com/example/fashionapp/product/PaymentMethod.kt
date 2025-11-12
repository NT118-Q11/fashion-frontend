package com.example.fashionapp.product

data class PaymentMethod(
    val id: Int,
    val name: String,
    val cardNumber: String = "",
    val iconRes: Int,
    var isSelected: Boolean = false
)


