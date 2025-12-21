package com.example.fashionapp.data

import com.example.fashionapp.model.FavoriteItem

object FavoritesManager {
    private val favorites = mutableListOf<FavoriteItem>()

    fun addFavorite(item: FavoriteItem) {
        if (favorites.none { it.id == item.id }) {
            favorites.add(item)
        }
    }

    fun removeFavorite(item: FavoriteItem) {
        favorites.removeAll { it.id == item.id }
    }

    fun getFavorites(): List<FavoriteItem> {
        return favorites.toList()
    }

    fun isFavorite(item: FavoriteItem): Boolean {
        return favorites.any { it.id == item.id }
    }
}

