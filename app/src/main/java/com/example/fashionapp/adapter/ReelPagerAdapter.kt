package com.example.fashionapp.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ItemReelBinding
import com.example.fashionapp.model.ReelItem

class ReelPagerAdapter(
    private val context: Context,
    private val items: MutableList<ReelItem> = mutableListOf()
) : RecyclerView.Adapter<ReelPagerAdapter.VH>() {

    inner class VH(val binding: ItemReelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.reelBrand.text = item.brand
        holder.binding.reelName.text = item.name
        holder.binding.reelPrice.text = item.priceText
        val path = item.imageAssetPath
        try {
            context.assets.open(path).use { input ->
                val bmp = BitmapFactory.decodeStream(input)
                holder.binding.reelImage.setImageBitmap(bmp)
                Log.d("ReelPagerAdapter", "Loaded asset: $path (${bmp.width}x${bmp.height})")
            }
        } catch (e: Exception) {
            Log.e("ReelPagerAdapter", "Failed to load asset: $path -> ${e.message}")
            holder.binding.reelImage.setImageResource(R.drawable.placeholder)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<ReelItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
}
