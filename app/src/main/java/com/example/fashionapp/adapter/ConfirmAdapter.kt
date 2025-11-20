package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.data.CartItem
import com.example.fashionapp.databinding.ItemConfirmProductBinding

class ConfirmAdapter(private val items: List<CartItem>) :
    RecyclerView.Adapter<ConfirmAdapter.ConfirmViewHolder>() {

    inner class ConfirmViewHolder(val binding: ItemConfirmProductBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConfirmViewHolder {
        val binding = ItemConfirmProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ConfirmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConfirmViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            tvProductName.text = item.title
            tvProductDesc.text = item.description
            tvQuantity.text = item.quantity.toString()
            tvPrice.text = "$${String.format("%.2f", item.price)}"
            imgProduct.setImageResource(item.imageRes)
        }
    }

    override fun getItemCount(): Int = items.size
}
