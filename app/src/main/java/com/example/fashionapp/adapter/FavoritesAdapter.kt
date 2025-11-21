package com.example.fashionapp.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.databinding.ItemFavoriteBinding
import com.example.fashionapp.model.FavoriteItem

class FavoritesAdapter(
    private val list: List<FavoriteItem>
) : RecyclerView.Adapter<FavoritesAdapter.FavViewHolder>() {

    inner class FavViewHolder(val binding: ItemFavoriteBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavViewHolder {
        val binding = ItemFavoriteBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FavViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavViewHolder, position: Int) {
        val item = list[position]

        holder.binding.imgProduct.setImageResource(item.image)
        holder.binding.tvProductName.text = item.name
        holder.binding.tvProductDesc.text = item.desc
        holder.binding.tvProductPrice.text = item.price
    }

    override fun getItemCount(): Int = list.size
}
