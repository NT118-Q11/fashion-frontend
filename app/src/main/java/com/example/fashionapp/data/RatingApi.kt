package com.example.fashionapp.data

import com.example.fashionapp.model.RatingRequest
import com.example.fashionapp.model.RatingResponse
import retrofit2.http.*

interface RatingApi {

    /**
     * Create a new rating
     * POST /api/ratings
     */
    @POST("/api/ratings")
    suspend fun createRating(@Body request: RatingRequest): RatingResponse

    /**
     * Get all ratings
     * GET /api/ratings
     */
    @GET("/api/ratings")
    suspend fun getAllRatings(): List<RatingResponse>

    /**
     * Get rating by ID
     * GET /api/ratings/{id}
     */
    @GET("/api/ratings/{id}")
    suspend fun getRatingById(@Path("id") id: String): RatingResponse

    /**
     * Get ratings for a product
     * GET /api/ratings/product/{productId}
     */
    @GET("/api/ratings/product/{productId}")
    suspend fun getRatingsByProduct(@Path("productId") productId: String): List<RatingResponse>

    /**
     * Get ratings by user
     * GET /api/ratings/user/{userId}
     */
    @GET("/api/ratings/user/{userId}")
    suspend fun getRatingsByUser(@Path("userId") userId: String): List<RatingResponse>

    /**
     * Update rating
     * PUT /api/ratings/{id}
     */
    @PUT("/api/ratings/{id}")
    suspend fun updateRating(
        @Path("id") id: String,
        @Body request: RatingRequest
    ): RatingResponse

    /**
     * Delete rating
     * DELETE /api/ratings/{id}
     */
    @DELETE("/api/ratings/{id}")
    suspend fun deleteRating(@Path("id") id: String): Map<String, String>
}

