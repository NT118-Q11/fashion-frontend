package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.data.Product
import com.example.fashionapp.databinding.ItemProductGridBinding
import com.example.fashionapp.R

class ProductAdapter(
    private val items: List<Product>,
    private val onFavoriteChanged: () -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductGridBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductGridBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val item = items[position]

        holder.binding.apply {

            tvProductName.text = item.title
            tvProductDesc.text = item.description
            tvProductPrice.text = "$${item.price}"
            imgProduct.setImageResource(item.imageRes)

            // Set icon tim
            ivLike.setImageResource(
                if (FavoritesManager.isFavorite(item.id))
                    R.drawable.ic_favorite_filled
                else
                    R.drawable.ic_favorite_filled
            )

            // Xử lý click tim
            ivLike.setOnClickListener {
                if (FavoritesManager.isFavorite(item.id)) {
                    FavoritesManager.removeFromFavorites(item.id)
                } else {
                    FavoritesManager.addToFavorites(item)
                }

                notifyItemChanged(holder.adapterPosition)
                onFavoriteChanged()
            }
        }
    }

    override fun getItemCount() = items.size
}
