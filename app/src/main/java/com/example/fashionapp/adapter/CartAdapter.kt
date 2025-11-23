package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.data.CartItem
import com.example.fashionapp.databinding.ItemCartBinding

class CartAdapter(
    private val items: MutableList<CartItem>,
    private val onQuantityChange: (List<CartItem>) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {

            tvTitle.text = item.title
            tvDesc.text = item.description
            tvPrice.text = "$${item.price}"
            tvQuantity.text = item.quantity.toString()
            imgProduct.setImageResource(item.imageRes)

            // Nút tăng
            btnPlus.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items[pos].quantity++
                    notifyItemChanged(pos)
                    onQuantityChange(items)
                }
            }

            // Nút giảm
            btnMinus.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    if (items[pos].quantity > 1) {
                        items[pos].quantity--
                        notifyItemChanged(pos)
                    } else {
                        // Nếu quantity = 1 thì xóa
                        removeItem(pos)
                    }
                    onQuantityChange(items)
                }
            }

            // Nút X để xóa khỏi giỏ
            btnRemove.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    removeItem(pos)
                    onQuantityChange(items)
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size

    // Hàm xóa item
    private fun removeItem(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, items.size)
    }
}
