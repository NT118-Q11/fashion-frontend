package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.data.Product
import com.example.fashionapp.databinding.ItemSearchBinding

class SearchAdapter(
    private var products: List<Product>,
    private val onFavoritesChanged: () -> Unit
) : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemSearchBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateList(newList: List<Product>) {
        products = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = products[position]

        holder.binding.apply {
            imgProduct.setImageResource(item.imageRes)
            txtTitle.text = item.title
            txtName.text = item.description
            txtPrice.text = "$${item.price}"

            val isFavorite = FavoritesManager.getFavorites().any { it.id == item.id }

            btnFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_border
            )

            btnFavorite.setOnClickListener {
                if (FavoritesManager.getFavorites().any { it.id == item.id }) {
                    FavoritesManager.removeFromFavorites(item.id)
                    btnFavorite.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    FavoritesManager.addToFavorites(item)
                    btnFavorite.setImageResource(R.drawable.ic_favorite_filled)
                }
                onFavoritesChanged()
            }
        }
    }
}
