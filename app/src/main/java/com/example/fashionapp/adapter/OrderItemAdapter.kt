package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.model.OrderItemResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val itemPrice = item.priceAtPurchase ?: item.price

        // Display product name (from response or fallback to product object)
        val productName = item.productName ?: item.product?.name ?: "Unknown Product"

        // Build variant info (size and color)
        val variantParts = mutableListOf<String>()
        item.size?.let { if (it.isNotEmpty()) variantParts.add("Size: $it") }
        item.color?.let { if (it.isNotEmpty()) variantParts.add("Color: $it") }
        val variantInfo = if (variantParts.isNotEmpty()) variantParts.joinToString(" | ") else ""

        // Set product name with variant info
        if (variantInfo.isNotEmpty()) {
            holder.tvProductName.text = "$productName\n$variantInfo"
        } else {
            holder.tvProductName.text = productName
        }

        holder.tvProductPrice.text = "$${String.format("%.2f", itemPrice)}"
        holder.tvQuantity.text = "x${item.quantity}"

        val subtotal = itemPrice * item.quantity
        holder.tvSubtotal.text = "$${String.format("%.2f", subtotal)}"

        // Set placeholder initially
        holder.ivProductImage.setImageResource(R.drawable.sample_woman)

        // Load product image from assets asynchronously
        val imagePath = item.product?.getThumbnailAssetPath()
        if (imagePath != null) {
            val context = holder.itemView.context
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        context.assets.open(imagePath).use { input ->
                            BitmapFactory.decodeStream(input)
                        }
                    }
                    holder.ivProductImage.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    holder.ivProductImage.setImageResource(R.drawable.sample_woman)
                }
            }
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<OrderItemResponse>) {
        items = newItems
        notifyDataSetChanged()
    }
}

