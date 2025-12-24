package com.example.fashionapp.model


/**
 * Request to create a new order
 */
data class OrderRequest(
    val userId: String,
    val items: List<OrderItemRequest>,
    val totalPrice: Double,
    val shippingAddress: String,
    val paymentMethod: String = "COD",
    val status: String = "PENDING"
)

/**
 * Request for order item
 */
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
    val price: Double
)

/**
 * Response for order item from backend
 */
data class OrderItemResponse(
    val id: String?,
    val orderId: String?,
    val productId: String,
    val quantity: Int,
    val price: Double,
    val product: Product?
)

/**
 * Response for order from backend
 */
data class OrderResponse(
    val id: String?,
    val userId: String,
    val items: List<OrderItemResponse> = emptyList(),
    val totalAmount: Double,
    val shippingAddress: String,
    val paymentMethod: String,
    val status: String,
    val createdAt: String?,
    val updatedAt: String?
)


