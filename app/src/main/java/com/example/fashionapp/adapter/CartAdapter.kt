package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemCartBinding
import com.example.fashionapp.uix.CartFragment

class CartAdapter(private val items: List<CartFragment.CartItem>) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = items[position]
        holder.binding.apply {

            tvTitle.text = currentItem.title
            tvDesc.text = currentItem.description
            tvQuantity.text = currentItem.quantity.toString()
            tvPrice.text = "$${currentItem.price}"
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
    