package com.example.fashionapp.data

object FavoritesManager {
    private val favoriteList = mutableListOf<Product>()

    fun addToFavorites(product: Product) {
        if (favoriteList.none { it.id == product.id }) {
            favoriteList.add(product)
        }
    }

    fun removeFromFavorites(product: Product) {
        favoriteList.removeAll { it.id == product.id }
    }

    fun removeFromFavorites(productId: Int) {
        favoriteList.removeAll { it.id == productId }
    }

    fun isFavorite(product: Product): Boolean {
        return favoriteList.any { it.id == product.id }
    }

    fun isFavorite(productId: Int): Boolean {
        return favoriteList.any { it.id == productId }
    }

    fun getFavorites(): List<Product> = favoriteList.toList()
}
