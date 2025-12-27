package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName

/**
 * Request to add an item to the cart
 */
data class AddToCartRequest(
    val userId: String,
    val productId: String,
    val quantity: Int,
    val selectedSize: String? = null,
    val selectedColor: String? = null
)

/**
 * Request to update an item's quantity in the cart
 */
data class UpdateCartItemRequest(
    val quantity: Int
)

/**
 * Represents an item within the cart, returned by the backend.
 * Note: The backend returns full product details within each cart item.
 */
data class CartItemResponse(
    val id: String,
    val productId: String,
    val quantity: Int,
    @SerializedName("selectedSize")
    val selectedSize: String? = null,
    @SerializedName("selectedColor")
    val selectedColor: String? = null,
    @SerializedName("size")
    val size: String? = null,
    @SerializedName("color")
    val color: String? = null,
    val product: Product?
) {
    /**
     * Get selected size - only return the size user selected, not all available sizes
     */
    fun getDisplaySize(): String? = selectedSize ?: size

    /**
     * Get selected color - only return the color user selected, not all available colors
     */
    fun getDisplayColor(): String? = selectedColor ?: color
}

/**
 * Represents the user's cart
 */
data class Cart(
    val id: String?,
    val userId: String,
    val items: List<CartItemResponse> = emptyList(),
    val totalPrice: Double = 0.0
)

/**
 * Generic response wrapper for cart operations
 */
data class CartResponse(
    val message: String,
    val cart: Cart?
)
