package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ItemProductRatingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Data class representing a product to be rated
 */
data class ProductRatingItem(
    val productId: String,
    val productName: String,
    val thumbnail: String?,
    val size: String?,
    val color: String?,
    var rating: Int = 0,
    var comment: String = ""
)

/**
 * Adapter for displaying products that need to be rated
 */
class ProductRatingAdapter(
    private val items: MutableList<ProductRatingItem>
) : RecyclerView.Adapter<ProductRatingAdapter.ProductRatingViewHolder>() {

    inner class ProductRatingViewHolder(val binding: ItemProductRatingBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Lưu trữ Watcher để có thể gỡ bỏ khi tái sử dụng ViewHolder
        var commentTextWatcher: TextWatcher? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductRatingViewHolder {
        val binding = ItemProductRatingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ProductRatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductRatingViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {
            // 1. Dọn dẹp TextWatcher cũ để tránh ghi đè dữ liệu sai vị trí
            holder.commentTextWatcher?.let { etComment.removeTextChangedListener(it) }

            // 2. Hiển thị thông tin sản phẩm
            tvProductName.text = item.productName
            val variantParts = mutableListOf<String>()
            item.size?.let { variantParts.add("Size: $it") }
            item.color?.let { variantParts.add("Color: $it") }

            if (variantParts.isNotEmpty()) {
                tvProductVariant.text = variantParts.joinToString(" | ")
                tvProductVariant.visibility = View.VISIBLE
            } else {
                tvProductVariant.visibility = View.GONE
            }

            // 3. Load ảnh
            loadProductImage(holder, item.thumbnail)

            // 4. Thiết lập RatingBar
            ratingBar.rating = item.rating.toFloat()
            ratingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
                if (fromUser) {
                    item.rating = rating.toInt()
                    updateRatingStatus(holder, item.rating)
                }
            }

            // 5. Thiết lập EditText và TextWatcher (Cập nhật Real-time)
            etComment.setText(item.comment)

            holder.commentTextWatcher = object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Lưu trực tiếp vào model ngay khi người dùng gõ
                    item.comment = s.toString().trim()
                }
                override fun afterTextChanged(s: Editable?) {}
            }
            etComment.addTextChangedListener(holder.commentTextWatcher)

            // 6. Cập nhật icon trạng thái
            updateRatingStatus(holder, item.rating)
        }
    }

    private fun loadProductImage(holder: ProductRatingViewHolder, thumbnail: String?) {
        if (thumbnail.isNullOrEmpty()) {
            holder.binding.imgProduct.setImageResource(R.drawable.sample_woman)
            return
        }

        val assetPath = extractAssetPath(thumbnail)
        if (assetPath != null) {
            val context = holder.binding.root.context
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val bitmap = withContext(Dispatchers.IO) {
                        context.assets.open(assetPath).use { input ->
                            BitmapFactory.decodeStream(input)
                        }
                    }
                    holder.binding.imgProduct.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    holder.binding.imgProduct.setImageResource(R.drawable.sample_woman)
                }
            }
        } else {
            holder.binding.imgProduct.setImageResource(R.drawable.sample_woman)
        }
    }

    private fun extractAssetPath(thumbnail: String): String? {
        val assetsIndex = thumbnail.indexOf("assets\\")
        if (assetsIndex != -1) {
            return thumbnail.substring(assetsIndex + 7).replace("\\", "/")
        }
        return if (!thumbnail.contains("\\")) thumbnail else null
    }

    private fun updateRatingStatus(holder: ProductRatingViewHolder, rating: Int) {
        holder.binding.imgRatingStatus.apply {
            if (rating > 0) {
                setImageResource(R.drawable.yellow_star)
                visibility = View.VISIBLE
            } else {
                visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = items.size

    fun getRatedProducts(): List<ProductRatingItem> = items.filter { it.rating > 0 }

    fun updateItems(newItems: List<ProductRatingItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}