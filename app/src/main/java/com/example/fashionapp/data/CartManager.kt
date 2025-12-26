package com.example.fashionapp.data

import android.util.Log
import com.example.fashionapp.AppRoute
import com.example.fashionapp.model.AddToCartRequest
import com.example.fashionapp.model.Cart
import com.example.fashionapp.model.CartItemResponse
import com.example.fashionapp.model.UpdateCartItemRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CartManager {

    private const val TAG = "CartManager"

    /**
     * Get user's cart from API
     */
    suspend fun getCart(userId: String): Cart? = withContext(Dispatchers.IO) {
        return@withContext try {
            AppRoute.cart.getCart(userId)
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching cart for user $userId", e)
            null
        }
    }

    /**
     * Add product to cart
     */
    suspend fun addToCart(
        userId: String,
        productId: String,
        quantity: Int,
        selectedSize: String? = null,
        selectedColor: String? = null
    ): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = AddToCartRequest(userId, productId, quantity, selectedSize, selectedColor)
            val response = AppRoute.cart.addToCart(request)
            Log.d(TAG, "Add to cart response: ${response.message}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding to cart: $productId", e)
            false
        }
    }

    /**
     * Update item quantity
     */
    suspend fun updateQuantity(itemId: String, userId: String, quantity: Int): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val request = UpdateCartItemRequest(quantity)
            val response = AppRoute.cart.updateCartItem(itemId, userId, request)
            Log.d(TAG, "Update quantity response: ${response.message}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating quantity for item: $itemId", e)
            false
        }
    }

    /**
     * Remove item from cart
     */
    suspend fun removeItem(itemId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = AppRoute.cart.removeCartItem(itemId, userId)
            Log.d(TAG, "Remove item response: ${response.message}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error removing item: $itemId", e)
            false
        }
    }

    /**
     * Clear entire cart
     */
    suspend fun clearCart(userId: String): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val response = AppRoute.cart.clearCart(userId)
            Log.d(TAG, "Clear cart response: ${response.message}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing cart for user $userId", e)
            false
        }
    }

    /**
     * LEGACY Compatibility: Calculate total from a list of items
     */
    fun calculateTotal(items: List<CartItemResponse>): Double {
        return items.sumOf { (it.product?.price ?: 0.0) * it.quantity }
    }
}
