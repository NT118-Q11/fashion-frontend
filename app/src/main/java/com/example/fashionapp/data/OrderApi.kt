package com.example.fashionapp.data

import com.example.fashionapp.model.*
import retrofit2.http.*

interface OrderApi {

    /**
     * Create a new order
     * POST /api/orders
     */
    @POST("/api/orders")
    suspend fun createOrder(@Body request: OrderRequest): OrderResponse

    /**
     * Get all orders
     * GET /api/orders
     */
    @GET("/api/orders")
    suspend fun getAllOrders(): List<OrderResponse>

    /**
     * Get order by ID
     * GET /api/orders/{id}
     */
    @GET("/api/orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): OrderResponse

    /**
     * Get all orders for a specific user
     * GET /api/orders/user/{userId}
     */
    @GET("/api/orders/user/{userId}")
    suspend fun getOrdersByUserId(@Path("userId") userId: String): List<OrderResponse>

    /**
     * Get all orders by status
     * GET /api/orders/status/{status}
     */
    @GET("/api/orders/status/{status}")
    suspend fun getOrdersByStatus(@Path("status") status: String): List<OrderResponse>

    /**
     * Update an order
     * PUT /api/orders/{id}
     */
    @PUT("/api/orders/{id}")
    suspend fun updateOrder(
        @Path("id") id: String,
        @Body request: OrderRequest
    ): OrderResponse

    /**
     * Delete an order
     * DELETE /api/orders/{id}
     */
    @DELETE("/api/orders/{id}")
    suspend fun deleteOrder(@Path("id") id: String): Map<String, String>
}

