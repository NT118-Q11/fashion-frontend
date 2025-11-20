
package com.example.fashionapp.data

object FavoritesManager {
    private val favoriteList = mutableListOf<Product>()

    fun addToFavorites(product: Product) {
        if (!favoriteList.any { it.id == product.id }) {
            favoriteList.add(product)
        }
    }

    // Accept a Product
    fun removeFromFavorites(product: Product) {
        favoriteList.removeAll { it.id == product.id }
    }

    // Accept an Int id
    fun removeFromFavorites(productId: Int) {
        favoriteList.removeAll { it.id == productId }
    }

    // Check by id
    fun isFavorite(productId: Int): Boolean {
        return favoriteList.any { it.id == productId }
    }

    // Check by Product
    fun isFavorite(product: Product): Boolean {
        return favoriteList.any { it.id == product.id }
    }

    fun getFavorites(): List<Product> = favoriteList.toList()
}
