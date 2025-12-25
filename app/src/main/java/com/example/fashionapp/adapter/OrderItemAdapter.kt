package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.model.OrderItemResponse

class OrderItemAdapter(
    private var items: List<OrderItemResponse>
) : RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder>() {

    inner class OrderItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivProductImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvSubtotal: TextView = itemView.findViewById(R.id.tvSubtotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_item, parent, false)
        return OrderItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderItemViewHolder, position: Int) {
        val item = items[position]

        holder.tvProductName.text = item.product?.name ?: "Product #${item.productId}"
        holder.tvProductPrice.text = "$${String.format("%.2f", item.price)}"
        holder.tvQuantity.text = "x${item.quantity}"

        val subtotal = item.price * item.quantity
        holder.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"

        // Load product image if available
        val imagePath = item.product?.getThumbnailAssetPath()
        if (imagePath != null) {
            val resourceId = holder.itemView.context.resources.getIdentifier(
                imagePath.replace("/", "_").replace(".jpg", "").replace(".png", ""),
                "drawable",
                holder.itemView.context.packageName
            )
            if (resourceId != 0) {
                holder.ivProductImage.setImageResource(resourceId)
            } else {
                holder.ivProductImage.setImageResource(R.drawable.sample_woman)
            }
        } else {
            holder.ivProductImage.setImageResource(R.drawable.sample_woman)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<OrderItemResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}

