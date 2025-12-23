package com.example.fashionapp.data

import retrofit2.http.*

/**
 * Favorite API interface matching backend endpoints
 */
interface FavoriteApi {

    /**
     * Add product to favorites
     * POST /api/favorites
     */
    @POST("/api/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): FavoriteResponse

    /**
     * Remove product from favorites
     * DELETE /api/favorites?userId={userId}&productId={productId}
     */
    @DELETE("/api/favorites")
    suspend fun removeFavorite(
        @Query("userId") userId: String,
        @Query("productId") productId: String
    ): MessageResponse

    /**
     * Get user's favorites
     * GET /api/favorites?userId={userId}
     */
    @GET("/api/favorites")
    suspend fun getFavorites(@Query("userId") userId: String): List<FavoriteResponse>

    /**
     * Check if product is favorited
     * GET /api/favorites/check?userId={userId}&productId={productId}
     */
    @GET("/api/favorites/check")
    suspend fun checkFavorite(
        @Query("userId") userId: String,
        @Query("productId") productId: String
    ): FavoriteCheckResponse
}

/**
 * Request model for adding favorite
 */
data class FavoriteRequest(
    val userId: String,
    val productId: String
)

/**
 * Response model for favorite operations
 */
data class FavoriteResponse(
    val id: String,
    val userId: String,
    val productId: String,
    val createdAt: String
)

/**
 * Response model for checking favorite status
 */
data class FavoriteCheckResponse(
    val isFavorite: Boolean
)

/**
 * Generic message response
 */
data class MessageResponse(
    val message: String
)

