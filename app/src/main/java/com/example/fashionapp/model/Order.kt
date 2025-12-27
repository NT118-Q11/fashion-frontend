package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName

/**
 * Request to create a new order
 */
data class OrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>,
    val totalPrice: Double,
    val shippingAddress: String,
    val paymentMethod: String = "COD",
    val status: String = "SUCCESSFUL"
)

/**
 * Request for order item
 */
data class OrderItemRequest(
    val orderId: String? = null,
    val productId: String,
    val productName: String? = null,
    val size: String? = null,
    val color: String? = null,
    val quantity: Int,
    val priceAtPurchase: Double
)

/**
 * Response for order item from backend
 */
data class OrderItemResponse(
    val id: String?,
    val orderId: String?,
    val productId: String,
    val productName: String? = null,
    val size: String? = null,
    val color: String? = null,
    val quantity: Int,
    val priceAtPurchase: Double? = null,
    val price: Double = priceAtPurchase ?: 0.0,
    val product: Product? = null
)

/**
 * Response for order from backend
 */
data class OrderResponse(
    val id: String?,
    val userId: String,
    val items: List<OrderItemResponse> = emptyList(),
    @SerializedName("totalAmount")
    val totalAmount: Double? = null,
    @SerializedName("totalPrice")
    val totalPrice: Double? = null,
    val shippingAddress: String,
    val paymentMethod: String,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
) {
    /**
     * Get total - handle both field names from backend
     */
    fun getTotal(): Double = totalAmount ?: totalPrice ?: 0.0
}


