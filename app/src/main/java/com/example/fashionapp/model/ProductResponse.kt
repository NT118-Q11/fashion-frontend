package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName

/**
 * DTO model that matches the backend ProductResponse exactly
 * Backend returns color and size as pipe-delimited strings
 */
data class ProductResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val category: String?,
    val brand: String?,
    val gender: String?,
    val size: String?,        // Backend format: "S|M|L" or "M"
    val color: String?,       // Backend format: "Gray|Black|Beige" or "Gray"
    val images: List<String>?,
    val thumbnail: String?,
    val stock: Int?,
    @SerializedName("createdAt")
    val createdAt: String?,
    @SerializedName("updatedAt")
    val updatedAt: String?
)

/**
 * Mapper to convert backend ProductResponse DTO to domain Product model
 */
fun ProductResponse.toDomain(): Product {
    // All available sizes in order
    val allSizes = listOf("S", "M", "L", "XL", "XXL")

    // Parse sizes from pipe-delimited string
    val parsedSizes = parseDelimitedString(size)
        ?.map { it.uppercase().trim() }
        ?.filter { it in allSizes }
        ?.takeIf { it.isNotEmpty() }

    // Parse colors from pipe-delimited string
    val parsedColors = parseDelimitedString(color)
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        ?.takeIf { it.isNotEmpty() }

    return Product(
        id = id,
        name = name,
        description = description,
        price = price,
        category = category,
        brand = brand,
        gender = gender,
        sizes = parsedSizes,
        colors = parsedColors,
        images = images,
        thumbnail = thumbnail,
        stock = stock,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

/**
 * Parse pipe-delimited string into list
 * Handles edge cases: null, blank, extra separators ("S||M|" -> ["S", "M"])
 */
private fun parseDelimitedString(value: String?): List<String>? {
    if (value.isNullOrBlank()) return null

    return value.split('|')
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .takeIf { it.isNotEmpty() }
}

