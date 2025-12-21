package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemFavoriteBinding
import com.example.fashionapp.model.FavoriteItem

class FavoritesAdapter(
    private val items: List<FavoriteItem>,
    private val onRemoveClick: (FavoriteItem) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvProductName.text = item.name
            tvProductDesc.text = item.desc
            tvProductPrice.text = item.price

            if (item.imageRes != 0) {
                imgProduct.setImageResource(item.imageRes)
            } else if (item.imagePath.isNotEmpty()) {
                try {
                    val inputStream = holder.itemView.context.assets.open(item.imagePath)
                    val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
                    imgProduct.setImageDrawable(drawable)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            ivLike.setOnClickListener {
                onRemoveClick(item)
            }
        }
    }

    override fun getItemCount() = items.size
}