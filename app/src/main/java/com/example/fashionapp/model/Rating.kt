package com.example.fashionapp.model

/**
 * Request to create a new rating
 */
data class RatingRequest(
    val productId: String,
    val userId: String,
    val rateStars: Int,
    val comment: String? = null
)

/**
 * Response for rating from backend
 */
data class RatingResponse(
    val id: String,
    val productId: String,
    val userId: String,
    val rateStars: Int,
    val comment: String?,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

