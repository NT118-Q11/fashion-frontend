package com.example.fashionapp.data

import com.example.fashionapp.model.OrderItemRequest
import com.example.fashionapp.model.OrderItemResponse
import retrofit2.http.*

/**
 * API interface for OrderItem endpoints
 */
interface OrderItemApi {

    /**
     * Create a new order item
     * POST /api/order-items
     */
    @POST("/api/order-items")
    suspend fun createOrderItem(@Body request: OrderItemRequest): OrderItemResponse

    /**
     * Get all order items
     * GET /api/order-items
     */
    @GET("/api/order-items")
    suspend fun getAllOrderItems(): List<OrderItemResponse>

    /**
     * Get order item by ID
     * GET /api/order-items/{id}
     */
    @GET("/api/order-items/{id}")
    suspend fun getOrderItemById(@Path("id") id: String): OrderItemResponse

    /**
     * Get all order items for a specific order
     * GET /api/order-items/order/{orderId}
     */
    @GET("/api/order-items/order/{orderId}")
    suspend fun getOrderItemsByOrderId(@Path("orderId") orderId: String): List<OrderItemResponse>

    /**
     * Get all order items for a specific product
     * GET /api/order-items/product/{productId}
     */
    @GET("/api/order-items/product/{productId}")
    suspend fun getOrderItemsByProductId(@Path("productId") productId: String): List<OrderItemResponse>

    /**
     * Update an order item
     * PUT /api/order-items/{id}
     */
    @PUT("/api/order-items/{id}")
    suspend fun updateOrderItem(
        @Path("id") id: String,
        @Body request: OrderItemRequest
    ): OrderItemResponse

    /**
     * Delete an order item
     * DELETE /api/order-items/{id}
     */
    @DELETE("/api/order-items/{id}")
    suspend fun deleteOrderItem(@Path("id") id: String): Map<String, String>
}

