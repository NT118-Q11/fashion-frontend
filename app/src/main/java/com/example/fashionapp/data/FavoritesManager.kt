package com.example.fashionapp.data

import android.content.Context
import android.util.Log
import com.example.fashionapp.AppRoute
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.async

/**
 * FavoritesManager handles favorite items with backend API integration
 */
class FavoritesManager private constructor(private val context: Context) {

    private val favorites = mutableListOf<FavoriteItem>()
    private val favoriteApi = AppRoute.favorite
    private val productApi = AppRoute.product
    private var currentUserId: String? = null

    companion object {
        private const val TAG = "FavoritesManager"

        @Volatile
        private var instance: FavoritesManager? = null

        fun getInstance(context: Context): FavoritesManager {
            return instance ?: synchronized(this) {
                instance ?: FavoritesManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Set the current user ID for API calls
     */
    fun setUserId(userId: String?) {
        currentUserId = userId
        if (userId != null) {
            loadFavoritesFromServer(userId)
        } else {
            favorites.clear()
        }
    }

    /**
     * Load favorites from server
     */
    private fun loadFavoritesFromServer(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteResponses = favoriteApi.getFavorites(userId)
                Log.d(TAG, "Loaded ${favoriteResponses.size} favorite IDs from server")

                // Fetch product details for each favorite
                val favoriteItems = favoriteResponses.mapNotNull { favoriteResponse ->
                    try {
                        val product = productApi.getProductById(favoriteResponse.productId)
                        convertProductToFavoriteItem(product)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load product ${favoriteResponse.productId}: ${e.message}")
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    // Clear and update local cache
                    favorites.clear()
                    favorites.addAll(favoriteItems)
                    Log.d(TAG, "Loaded ${favoriteItems.size} favorite products with details")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load favorites from server: ${e.message}")
            }
        }
    }

    /**
     * Convert Product to FavoriteItem
     */
    private fun convertProductToFavoriteItem(product: Product): FavoriteItem {
        return FavoriteItem(
            id = product.id,
            name = product.name,
            desc = product.description ?: "",
            price = "$${product.price}",
            imagePath = product.getThumbnailAssetPath() ?: ""
        )
    }

    /**
     * Add item to favorites (with API call)
     */
    fun addFavorite(item: FavoriteItem, onComplete: ((Boolean) -> Unit)? = null) {
        val userId = currentUserId
        if (userId == null) {
            Log.w(TAG, "Cannot add favorite: user not logged in")
            onComplete?.invoke(false)
            return
        }

        if (favorites.none { it.id == item.id }) {
            favorites.add(item)
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val request = FavoriteRequest(userId = userId, productId = item.id)
                val response = favoriteApi.addFavorite(request)
                Log.d(TAG, "Added favorite: ${response.productId}")
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add favorite: ${e.message}")
                // Rollback on error
                withContext(Dispatchers.Main) {
                    favorites.removeAll { it.id == item.id }
                    onComplete?.invoke(false)
                }
            }
        }
    }

    /**
     * Add favorite by product ID (fetches product details first)
     */
    fun addFavoriteByProductId(productId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val userId = currentUserId
        if (userId == null) {
            Log.w(TAG, "Cannot add favorite: user not logged in")
            onComplete?.invoke(false)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // First fetch product details
                val product = productApi.getProductById(productId)
                val favoriteItem = convertProductToFavoriteItem(product)

                // Add to local cache optimistically
                withContext(Dispatchers.Main) {
                    if (favorites.none { it.id == favoriteItem.id }) {
                        favorites.add(favoriteItem)
                    }
                }

                // Then add to backend
                val request = FavoriteRequest(userId = userId, productId = productId)
                val response = favoriteApi.addFavorite(request)
                Log.d(TAG, "Added favorite: ${response.productId}")

                withContext(Dispatchers.Main) {
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to add favorite by product ID: ${e.message}")
                // Rollback on error
                withContext(Dispatchers.Main) {
                    favorites.removeAll { it.id == productId }
                    onComplete?.invoke(false)
                }
            }
        }
    }

    /**
     * Remove item from favorites (with API call)
     */
    fun removeFavorite(item: FavoriteItem, onComplete: ((Boolean) -> Unit)? = null) {
        val userId = currentUserId
        if (userId == null) {
            Log.w(TAG, "Cannot remove favorite: user not logged in")
            onComplete?.invoke(false)
            return
        }

        favorites.removeAll { it.id == item.id }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                favoriteApi.removeFavorite(userId = userId, productId = item.id)
                Log.d(TAG, "Removed favorite: ${item.id}")
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove favorite: ${e.message}")
                // Rollback on error
                withContext(Dispatchers.Main) {
                    if (favorites.none { it.id == item.id }) {
                        favorites.add(item)
                    }
                    onComplete?.invoke(false)
                }
            }
        }
    }

    /**
     * Remove favorite by product ID
     */
    fun removeFavoriteByProductId(productId: String, onComplete: ((Boolean) -> Unit)? = null) {
        val userId = currentUserId
        if (userId == null) {
            Log.w(TAG, "Cannot remove favorite: user not logged in")
            onComplete?.invoke(false)
            return
        }

        favorites.removeAll { it.id == productId }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                favoriteApi.removeFavorite(userId = userId, productId = productId)
                Log.d(TAG, "Removed favorite: $productId")
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to remove favorite by product ID: ${e.message}")
                // Rollback would require re-fetching product, skip for now
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(false)
                }
            }
        }
    }

    /**
     * Reload favorites from server with callback
     */
    fun reloadFavorites(onComplete: ((Boolean) -> Unit)? = null) {
        val userId = currentUserId
        if (userId == null) {
            Log.w(TAG, "Cannot reload favorites: user not logged in")
            onComplete?.invoke(false)
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val favoriteResponses = favoriteApi.getFavorites(userId)
                Log.d(TAG, "Reloaded ${favoriteResponses.size} favorite IDs from server")

                // Fetch product details for each favorite
                val favoriteItems = favoriteResponses.mapNotNull { favoriteResponse ->
                    try {
                        val product = productApi.getProductById(favoriteResponse.productId)
                        convertProductToFavoriteItem(product)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load product ${favoriteResponse.productId}: ${e.message}")
                        null
                    }
                }

                withContext(Dispatchers.Main) {
                    // Clear and update local cache
                    favorites.clear()
                    favorites.addAll(favoriteItems)
                    Log.d(TAG, "Reloaded ${favoriteItems.size} favorite products with details")
                    onComplete?.invoke(true)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to reload favorites from server: ${e.message}")
                withContext(Dispatchers.Main) {
                    onComplete?.invoke(false)
                }
            }
        }
    }

    /**
     * Get all favorites (from local cache)
     */
    fun getFavorites(): List<FavoriteItem> {
        return favorites.toList()
    }

    /**
     * Check if item is in favorites (from local cache)
     */
    fun isFavorite(item: FavoriteItem): Boolean {
        return favorites.any { it.id == item.id }
    }

    /**
     * Check if product ID is in favorites (from local cache)
     */
    fun isFavorite(productId: String): Boolean {
        return favorites.any { it.id == productId }
    }

    /**
     * Toggle favorite status
     */
    fun toggleFavorite(item: FavoriteItem, onComplete: ((Boolean, Boolean) -> Unit)? = null) {
        if (isFavorite(item)) {
            removeFavorite(item) { success ->
                onComplete?.invoke(false, success)
            }
        } else {
            addFavorite(item) { success ->
                onComplete?.invoke(true, success)
            }
        }
    }
}

