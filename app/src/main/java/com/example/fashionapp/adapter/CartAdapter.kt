package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
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
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
