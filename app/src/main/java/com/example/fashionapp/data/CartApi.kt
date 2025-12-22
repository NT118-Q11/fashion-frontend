package com.example.fashionapp.data

import com.example.fashionapp.model.*
import retrofit2.http.*

interface CartApi {

    /**
     * Get user's cart
     * GET /api/cart?userId={userId}
     */
    @GET("/api/cart")
    suspend fun getCart(@Query("userId") userId: String): Cart

    /**
     * Add item to cart
     * POST /api/cart/items
     */
    @POST("/api/cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): CartResponse

    /**
     * Update cart item quantity
     * PUT /api/cart/items/{id}?userId={userId}
     */
    @PUT("/api/cart/items/{id}")
    suspend fun updateCartItem(
        @Path("id") id: String,
        @Query("userId") userId: String,
        @Body request: UpdateCartItemRequest
    ): CartResponse

    /**
     * Remove item from cart
     * DELETE /api/cart/items/{id}?userId={userId}
     */
    @DELETE("/api/cart/items/{id}")
    suspend fun removeCartItem(
        @Path("id") id: String,
        @Query("userId") userId: String
    ): CartResponse

    /**
     * Clear entire cart
     * DELETE /api/cart?userId={userId}
     */
    @DELETE("/api/cart")
    suspend fun clearCart(@Query("userId") userId: String): CartResponse
}
