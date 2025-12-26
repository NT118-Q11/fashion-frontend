package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName

/**
 * Product Information model matching backend ProductInformation entity
 * Contains additional product details like brand, category, gender, fit, care instructions, and origin
 */
data class ProductInformation(
    val id: String? = null,
    val productId: String,
    val brand: String,
    val category: String,
    val gender: String,
    val fit: String? = null,
    val care: String? = null,
    val origin: String? = null,
    @SerializedName("createdAt")
    val createdAt: String? = null,
    @SerializedName("updatedAt")
    val updatedAt: String? = null
) {
    /**
     * Format the product information as a readable string for display
     */
    fun toDisplayText(): String {
        val details = mutableListOf<String>()

        details.add("• Brand: $brand")
        details.add("• Category: $category")
        details.add("• Gender: $gender")

        fit?.let { details.add("• Fit: $it") }
        care?.let { details.add("• Care: $it") }
        origin?.let { details.add("• Origin: $it") }

        return details.joinToString("\n")
    }
}

/**
 * Request model for creating product information
 * Matches backend ProductInformationCreateRequest
 */
data class ProductInformationCreateRequest(
    val productId: String,
    val brand: String,
    val category: String,
    val gender: String,
    val fit: String? = null,
    val care: String? = null,
    val origin: String? = null
)

/**
 * Request model for updating product information
 * Matches backend ProductInformationUpdateRequest
 */
data class ProductInformationUpdateRequest(
    val brand: String? = null,
    val category: String? = null,
    val gender: String? = null,
    val fit: String? = null,
    val care: String? = null,
    val origin: String? = null
)

/**
 * Response wrapper for product information API responses
 */
data class ProductInformationApiResponse(
    val message: String,
    val productInformation: ProductInformation
)

/**
 * Response wrapper for delete operations
 */
data class ProductInformationDeleteResponse(
    val message: String
)

