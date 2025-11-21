package com.example.fashionapp.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.data.Product
import java.io.IOException

class SearchAdapter(
    private var items: List<Product>
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProduct: ImageView = itemView.findViewById(com.example.fashionapp.R.id.imgProduct)
        val txtTitle: TextView = itemView.findViewById(com.example.fashionapp.R.id.txtTitle)
        val txtName: TextView = itemView.findViewById(com.example.fashionapp.R.id.txtName)
        val txtPrice: TextView = itemView.findViewById(com.example.fashionapp.R.id.txtPrice)
        val btnFavorite: ImageButton = itemView.findViewById(com.example.fashionapp.R.id.btnFavorite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(com.example.fashionapp.R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val product = items[position]

        try {
            val am = holder.itemView.context.assets
            am.open(product.assetImage).use { input: java.io.InputStream ->
                val bmp = BitmapFactory.decodeStream(input)
                holder.imgProduct.setImageBitmap(bmp)
            }
        } catch (e: IOException) {
            holder.imgProduct.setImageResource(com.example.fashionapp.R.drawable.sample_woman)
        }

        holder.txtTitle.text = product.title
        holder.txtName.text = product.description
        holder.txtPrice.text = "$${product.price}"

        updateFavoriteIcon(holder, product)

        holder.btnFavorite.setOnClickListener {
            if (FavoritesManager.isFavorite(product)) {
                FavoritesManager.removeFromFavorites(product)
            } else {
                FavoritesManager.addToFavorites(product)
            }
            updateFavoriteIcon(holder, product)
        }
    }

    private fun updateFavoriteIcon(holder: SearchViewHolder, product: Product) {
        if (FavoritesManager.isFavorite(product)) {
            holder.btnFavorite.setImageResource(com.example.fashionapp.R.drawable.ic_favorite_filled)
        } else {
            holder.btnFavorite.setImageResource(com.example.fashionapp.R.drawable.ic_favorite_border)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateList(newList: List<Product>) {
        items = newList
        notifyDataSetChanged()
    }
}
