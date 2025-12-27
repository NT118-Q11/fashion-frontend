package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemConfirmProductBinding
import com.example.fashionapp.model.CartItemResponse
import com.example.fashionapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            
            // Display size and color if available
            val variantParts = mutableListOf<String>()
            item.getDisplaySize()?.let { variantParts.add("Size: $it") }
            item.getDisplayColor()?.let { variantParts.add("Color: $it") }

            if (variantParts.isNotEmpty()) {
                tvVariant.text = variantParts.joinToString(" | ")
                tvVariant.visibility = View.VISIBLE
            } else {
                tvVariant.visibility = View.GONE
            }

            // Set placeholder initially
            imgProduct.setImageResource(R.drawable.sample_woman)

            // Load product image from assets asynchronously
            val assetPath = product?.getThumbnailAssetPath()
            if (assetPath != null) {
                val context = root.context
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            context.assets.open(assetPath).use { input ->
                                BitmapFactory.decodeStream(input)
                            }
                        }
                        imgProduct.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        imgProduct.setImageResource(R.drawable.sample_woman)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
