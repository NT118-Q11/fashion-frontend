package com.example.fashionapp.adapter

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ItemImageSliderBinding

/**
 * Sealed class to represent different types of images
 */
sealed class ImageSource {
    data class DrawableResource(val resId: Int) : ImageSource()
    data class AssetPath(val path: String) : ImageSource()
}

class ImageSliderAdapter(
    private val images: List<Any>,
    private val context: Context? = null
) : RecyclerView.Adapter<ImageSliderAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: Any) {
            when (image) {
                is Int -> {
                    // Drawable resource ID
                    binding.sliderImageView.setImageResource(image)
                }
                is String -> {
                    // Asset path
                    loadImageFromAssets(image)
                }
                is ImageSource.DrawableResource -> {
                    binding.sliderImageView.setImageResource(image.resId)
                }
                is ImageSource.AssetPath -> {
                    loadImageFromAssets(image.path)
                }
            }
        }

        private fun loadImageFromAssets(path: String) {
            val ctx = context ?: binding.root.context
            try {
                ctx.assets.open(path).use { input ->
                    val bitmap = BitmapFactory.decodeStream(input)
                    binding.sliderImageView.setImageBitmap(bitmap)
                    Log.d("ImageSliderAdapter", "Loaded asset: $path (${bitmap.width}x${bitmap.height})")
                }
            } catch (e: Exception) {
                Log.e("ImageSliderAdapter", "Failed to load asset: $path -> ${e.message}")
                binding.sliderImageView.setImageResource(R.drawable.placeholder)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemImageSliderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}

