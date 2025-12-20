package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName

/**
 * Product model matching backend ProductResponse
 */
data class Product(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val brand: String?,
    val gender: String?,
    val sizes: List<String>?,
    val colors: List<String>?,
    val images: List<String>?,
    val stock: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Product create request model
 */
data class ProductCreateRequest(
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val brand: String?,
    val gender: String?,
    val sizes: List<String>?,
    val colors: List<String>?,
    val images: List<String>?,
    val stock: Int?
)

/**
 * Product update request model
 */
data class ProductUpdateRequest(
    val name: String?,
    val description: String?,
    val price: Double?,
    val category: String?,
    val brand: String?,
    val gender: String?,
    val sizes: List<String>?,
    val colors: List<String>?,
    val images: List<String>?,
    val stock: Int?
)

/**
 * Generic response for product operations
 */
data class ProductApiResponse(
    val message: String,
    val product: Product?
)

/**
 * Delete response
 */
data class DeleteResponse(
    val message: String
)

