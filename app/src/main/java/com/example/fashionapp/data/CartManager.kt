package com.example.fashionapp.data

object CartManager {

    private val items = mutableListOf<CartItem>()

    fun addItem(item: CartItem) {
        val existing = items.find { it.id == item.id }
        if (existing != null) {
            existing.quantity += item.quantity
        } else {
            items.add(item)
        }
    }

    fun getItems(): List<CartItem> {
        return items
    }

    fun getTotal(): Double {
        return items.sumOf { it.price * it.quantity }
    }

    fun removeItem(id: Int) {
        items.removeAll { it.id == id }
    }

    // 🔥 HÀM MỚI — GIẢI QUYẾT LỖI updateQuantities()
    fun updateQuantities(updatedList: List<CartItem>) {
        items.clear()
        items.addAll(updatedList)
    }
}
