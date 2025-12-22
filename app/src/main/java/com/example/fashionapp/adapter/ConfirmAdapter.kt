package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemConfirmProductBinding
import com.example.fashionapp.model.CartItemResponse
import com.example.fashionapp.R

class ConfirmAdapter(private val items: List<CartItemResponse>) :
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
        val product = item.product

        holder.binding.apply {
            tvProductName.text = product?.name ?: "Unknown"
            tvProductDesc.text = product?.description ?: ""
            tvQuantity.text = item.quantity.toString()
            tvPrice.text = "$${String.format("%.2f", product?.price ?: 0.0)}"
            
            // Load product image from assets
            val assetPath = product?.getThumbnailAssetPath()
            if (assetPath != null) {
                try {
                    val context = root.context
                    val inputStream = context.assets.open(assetPath)
                    val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
                    imgProduct.setImageDrawable(drawable)
                    inputStream.close()
                } catch (e: Exception) {
                    imgProduct.setImageResource(R.drawable.sample_woman)
                }
            } else {
                imgProduct.setImageResource(R.drawable.sample_woman)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
