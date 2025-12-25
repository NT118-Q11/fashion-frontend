package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.model.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for displaying products in a RecyclerView
 */
class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        private val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)
        private val favoritesManager = FavoritesManager.getInstance(itemView.context)

        fun bind(product: Product) {
            txtTitle.text = product.name
            txtPrice.text = "$${String.format("%.2f", product.price)}"

            // Set placeholder initially
            imgProduct.setImageResource(R.drawable.sample_woman)

            // Load thumbnail from assets asynchronously
            val thumbnailPath = product.getThumbnailAssetPath()
            if (!thumbnailPath.isNullOrEmpty()) {
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        val bitmap = withContext(Dispatchers.IO) {
                            itemView.context.assets.open(thumbnailPath).use { input ->
                                BitmapFactory.decodeStream(input)
                            }
                        }
                        imgProduct.setImageBitmap(bitmap)
                        Log.d("ProductAdapter", "Loaded thumbnail: $thumbnailPath")
                    } catch (e: Exception) {
                        Log.e("ProductAdapter", "Failed to load thumbnail: $thumbnailPath -> ${e.message}")
                        imgProduct.setImageResource(R.drawable.sample_woman)
                    }
                }
            }

            // Create favorite item from product
            val favItem = FavoriteItem(
                id = product.id,
                name = product.name,
                desc = product.description ?: product.brand ?: "",
                price = "$${String.format("%.2f", product.price)}",
                imagePath = product.getThumbnailAssetPath() ?: ""
            )

            // Update favorite button icon
            fun updateFavoriteIcon() {
                val isFav = favoritesManager.isFavorite(favItem)
                btnFavorite.setImageResource(
                    if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
                )
            }

            updateFavoriteIcon()

            // Handle favorite button click
            btnFavorite.setOnClickListener {
                favoritesManager.toggleFavorite(favItem) { isFavorite, success ->
                    if (success) {
                        updateFavoriteIcon()
                    } else {
                        Log.e("ProductAdapter", "Failed to toggle favorite")
                    }
                }
            }

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    /**
     * Update the product list and refresh the RecyclerView
     */
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}

