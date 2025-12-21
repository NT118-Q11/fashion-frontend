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
    val thumbnail: String?,
    val stock: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
) {
    /**
     * Extract asset path from Windows file path
     * Example: "C:\\Users\\tung\\...\\assets\\woman\\women6.jpg" -> "woman/women6.jpg"
     */
    fun getThumbnailAssetPath(): String? {
        if (thumbnail.isNullOrEmpty()) return null

        // Find the "assets" folder in the path and extract everything after it
        val assetsIndex = thumbnail.indexOf("assets\\")
        if (assetsIndex != -1) {
            // Extract path after "assets\" and convert backslashes to forward slashes
            return thumbnail.substring(assetsIndex + 7).replace("\\", "/")
        }

        // If already in correct format (no backslashes), return as is
        if (!thumbnail.contains("\\")) {
            return thumbnail
        }

        return null
    }
}

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

