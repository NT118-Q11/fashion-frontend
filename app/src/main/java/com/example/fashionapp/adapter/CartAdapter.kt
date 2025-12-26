package com.example.fashionapp.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemCartBinding
import com.example.fashionapp.model.CartItemResponse
import com.example.fashionapp.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CartAdapter(
    private val items: MutableList<CartItemResponse>,
    private val onIncrease: (CartItemResponse) -> Unit,
    private val onDecrease: (CartItemResponse) -> Unit,
    private val onRemove: (CartItemResponse) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    // Cache for product images to prevent flickering
    private val imageCache = mutableMapOf<String, Bitmap>()

    inner class CartViewHolder(val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        val product = item.product

        holder.binding.apply {
            tvTitle.text = product?.name ?: "Unknown Product"
            tvDesc.text = product?.description ?: ""
            tvPrice.text = "$${product?.price ?: 0.0}"
            tvQuantity.text = item.quantity.toString()

            // Display size and color if available
            val variantParts = mutableListOf<String>()
            item.selectedSize?.let { variantParts.add("Size: $it") }
            item.selectedColor?.let { variantParts.add("Color: $it") }

            if (variantParts.isNotEmpty()) {
                tvVariant.text = variantParts.joinToString(" | ")
                tvVariant.visibility = View.VISIBLE
            } else {
                tvVariant.visibility = View.GONE
            }

            // Load product image from assets with caching
            val assetPath = product?.getThumbnailAssetPath()
            val cacheKey = assetPath ?: product?.id ?: ""

            // Check cache first
            val cachedBitmap = imageCache[cacheKey]
            if (cachedBitmap != null) {
                imgProduct.setImageBitmap(cachedBitmap)
            } else if (assetPath != null) {
                // Keep current image while loading to prevent flash
                val context = root.context
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            context.assets.open(assetPath).use { input ->
                                BitmapFactory.decodeStream(input)
                            }
                        }
                        // Cache the bitmap
                        imageCache[cacheKey] = bitmap
                        imgProduct.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        imgProduct.setImageResource(R.drawable.sample_woman)
                    }
                }
            } else {
                imgProduct.setImageResource(R.drawable.sample_woman)
            }

            // Nút tăng
            btnPlus.setOnClickListener {
                onIncrease(item)
            }

            // Nút giảm
            btnMinus.setOnClickListener {
                onDecrease(item)
            }

            // Nút X để xóa khỏi giỏ
            btnRemove.setOnClickListener {
                onRemove(item)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<CartItemResponse>) {
        val diffCallback = CartDiffCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Clear the image cache when adapter is no longer needed
     */
    fun clearCache() {
        imageCache.clear()
    }

    /**
     * DiffUtil callback to optimize RecyclerView updates and prevent flickering
     */
    private class CartDiffCallback(
        private val oldList: List<CartItemResponse>,
        private val newList: List<CartItemResponse>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val old = oldList[oldItemPosition]
            val new = newList[newItemPosition]
            return old.id == new.id &&
                   old.quantity == new.quantity &&
                   old.selectedSize == new.selectedSize &&
                   old.selectedColor == new.selectedColor
        }
    }
}
