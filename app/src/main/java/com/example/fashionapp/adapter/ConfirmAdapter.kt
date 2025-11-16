package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.uix.ConfirmFragment
import com.example.fashionapp.databinding.ItemCartBinding


class ConfirmAdapter(private val items: List<ConfirmFragment.CartItem>) :
    RecyclerView.Adapter<ConfirmAdapter.CartViewHolder>() {


    inner class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        // Sử dụng LayoutInflater.from(parent.context) là cách thực hành tốt
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val currentItem = items[position]
        holder.binding.apply {
            // TODO: Cập nhật tên các view cho phù hợp với file item_cart.xml của bạn


            tvTitle.text = currentItem.title
            tvDesc.text = currentItem.description // Giả sử CartItem có thuộc tính description
            tvQuantity.text = currentItem.quantity.toString()
            tvPrice.text = "$${currentItem.price}"


        }
    }

    override fun getItemCount(): Int {

        return items.size
    }
}
