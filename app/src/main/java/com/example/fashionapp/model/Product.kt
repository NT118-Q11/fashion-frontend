package com.example.fashionapp.model

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonDeserializer
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonElement
import java.lang.reflect.Type

/**
 * Product model matching backend ProductResponse
 * Automatically handles pipe-delimited strings from backend (e.g., "S|M|L" or "Gray|Black")
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

    /**
     * Get all product images as asset paths
     * Handles both:
     * 1. Products with multiple images in a folder (e.g., woman/women1/women1_1.jpg)
     * 2. Products with single image (e.g., woman/women6.jpg)
     *
     * Returns list of asset paths ready to load from assets folder
     */
    fun getImageAssetPaths(assetManager: android.content.res.AssetManager): List<String> {
        val imagePaths = mutableListOf<String>()

        // First, try to get images from the images field if available
        if (!images.isNullOrEmpty()) {
            images.forEach { imagePath ->
                val assetPath = extractAssetPath(imagePath)
                if (assetPath != null) {
                    imagePaths.add(assetPath)
                }
            }
        }

        // If no images found, try to detect from thumbnail
        if (imagePaths.isEmpty() && !thumbnail.isNullOrEmpty()) {
            val thumbnailPath = getThumbnailAssetPath()
            if (thumbnailPath != null) {
                // Check if this is a folder-based product (e.g., woman/women1/women1_1.jpg)
                // or single image (e.g., woman/women6.jpg)
                val pathParts = thumbnailPath.split("/")

                if (pathParts.size >= 3) {
                    // Folder-based product: woman/women1/women1_1.jpg
                    val category = pathParts[0]  // woman
                    val productFolder = pathParts[1]  // women1
                    val folderPath = "$category/$productFolder"

                    try {
                        // List all images in the product folder
                        val files = assetManager.list(folderPath)
                        if (!files.isNullOrEmpty()) {
                            files.forEach { fileName ->
                                if (fileName.endsWith(".jpg", ignoreCase = true) ||
                                    fileName.endsWith(".png", ignoreCase = true)) {
                                    imagePaths.add("$folderPath/$fileName")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        // If folder doesn't exist, fall back to thumbnail
                        imagePaths.add(thumbnailPath)
                    }
                } else {
                    // Single image product: woman/women6.jpg
                    imagePaths.add(thumbnailPath)
                }
            }
        }

        // If still no images, return empty list
        return imagePaths
    }

    /**
     * Extract asset path from Windows file path or return as-is if already in correct format
     */
    private fun extractAssetPath(path: String): String? {
        if (path.isEmpty()) return null

        // Find the "assets" folder in the path and extract everything after it
        val assetsIndex = path.indexOf("assets\\")
        if (assetsIndex != -1) {
            return path.substring(assetsIndex + 7).replace("\\", "/")
        }

        // Also try forward slash variant
        val assetsIndexForward = path.indexOf("assets/")
        if (assetsIndexForward != -1) {
            return path.substring(assetsIndexForward + 7)
        }

        // If already in correct format (no absolute path), return as is
        if (!path.contains(":\\") && !path.startsWith("/")) {
            return path.replace("\\", "/")
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

/**
 * Custom Gson deserializer for Product to handle backend pipe-delimited strings
 * Backend returns: color: "Gray|Black|Beige", size: "S|M|L"
 * This converts them to: colors: ["Gray", "Black", "Beige"], sizes: ["S", "M", "L"]
 */
class ProductDeserializer : JsonDeserializer<Product> {
    private val allSizes = listOf("S", "M", "L", "XL", "XXL")

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Product {
        val jsonObject = json.asJsonObject

        // Parse sizes from pipe-delimited string or array
        val sizes = when {
            jsonObject.has("sizes") && !jsonObject.get("sizes").isJsonNull -> {
                // Already an array
                context.deserialize<List<String>>(jsonObject.get("sizes"), object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
                    ?.map { it.uppercase().trim() }
                    ?.filter { it in allSizes }
            }
            jsonObject.has("size") && !jsonObject.get("size").isJsonNull -> {
                // Pipe-delimited string
                val sizeStr = jsonObject.get("size").asString
                parseDelimitedString(sizeStr)
                    ?.map { it.uppercase().trim() }
                    ?.filter { it in allSizes }
            }
            else -> null
        }

        // Parse colors from pipe-delimited string or array
        val colors = when {
            jsonObject.has("colors") && !jsonObject.get("colors").isJsonNull -> {
                // Already an array
                context.deserialize<List<String>>(jsonObject.get("colors"), object : com.google.gson.reflect.TypeToken<List<String>>() {}.type)
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
            }
            jsonObject.has("color") && !jsonObject.get("color").isJsonNull -> {
                // Pipe-delimited string
                val colorStr = jsonObject.get("color").asString
                parseDelimitedString(colorStr)
                    ?.map { it.trim() }
                    ?.filter { it.isNotEmpty() }
            }
            else -> null
        }

        // Parse other fields normally
        return Product(
            id = jsonObject.get("id").asString,
            name = jsonObject.get("name").asString,
            description = jsonObject.get("description")?.takeIf { !it.isJsonNull }?.asString,
            price = jsonObject.get("price").asDouble,
            category = jsonObject.get("category")?.takeIf { !it.isJsonNull }?.asString,
            brand = jsonObject.get("brand")?.takeIf { !it.isJsonNull }?.asString,
            gender = jsonObject.get("gender")?.takeIf { !it.isJsonNull }?.asString,
            sizes = sizes,
            colors = colors,
            images = context.deserialize(jsonObject.get("images"), object : com.google.gson.reflect.TypeToken<List<String>>() {}.type),
            thumbnail = jsonObject.get("thumbnail")?.takeIf { !it.isJsonNull }?.asString,
            stock = jsonObject.get("stock")?.takeIf { !it.isJsonNull }?.asInt,
            createdAt = jsonObject.get("createdAt")?.takeIf { !it.isJsonNull }?.asString,
            updatedAt = jsonObject.get("updatedAt")?.takeIf { !it.isJsonNull }?.asString
        )
    }

    private fun parseDelimitedString(value: String?): List<String>? {
        if (value.isNullOrBlank()) return null

        return value.split('|')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .takeIf { it.isNotEmpty() }
    }
}
