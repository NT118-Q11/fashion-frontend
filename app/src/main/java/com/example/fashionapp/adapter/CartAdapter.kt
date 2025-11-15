package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.CartItem

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: () -> Unit,
    private val onItemRemoved: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgCartProduct)
        val tvBrand: TextView = view.findViewById(R.id.tvCartBrand)
        val tvProductName: TextView = view.findViewById(R.id.tvCartProductName)
        val tvPrice: TextView = view.findViewById(R.id.tvCartPrice)
        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        val btnPlus: TextView = view.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount() = cartItems.size

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]
        
        holder.imgProduct.setImageResource(item.imageRes)
        holder.tvBrand.text = item.brand
        holder.tvProductName.text = item.name
        holder.tvPrice.text = "$${item.price}"
        holder.tvQuantity.text = item.quantity.toString()

        // Handle minus button - remove item when quantity reaches 0
        holder.btnMinus.setOnClickListener {
            if (item.quantity > 1) {
                item.quantity--
                holder.tvQuantity.text = item.quantity.toString()
                onQuantityChanged()
            } else {
                // Remove item when quantity reaches 0
                val position = holder.bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    cartItems.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cartItems.size)
                    onItemRemoved()
                }
            }
        }

        // Handle plus button
        holder.btnPlus.setOnClickListener {
            item.quantity++
            holder.tvQuantity.text = item.quantity.toString()
            onQuantityChanged()
        }
    }

    fun getTotalPrice(): Int {
        return cartItems.sumOf { it.price * it.quantity }
    }

    fun clearAll() {
        val size = cartItems.size
        cartItems.clear()
        notifyItemRangeRemoved(0, size)
    }
}

