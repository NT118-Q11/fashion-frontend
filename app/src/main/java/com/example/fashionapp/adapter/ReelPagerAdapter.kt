package com.example.fashionapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.databinding.ItemReelBinding
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.model.ReelItem
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow

class ReelPagerAdapter(
    private val context: Context,
    private val items: MutableList<ReelItem> = mutableListOf()
) : RecyclerView.Adapter<ReelPagerAdapter.VH>() {

    // Listener for top area text color suggestions (left, center, right)
    var onTopTextColorsSuggested: ((Int, Int, Int) -> Unit)? = null

    // Listener for item clicks
    var onItemClick: ((ReelItem) -> Unit)? = null

    private val topColorCache: MutableMap<Int, Triple<Int, Int, Int>> = ConcurrentHashMap()

    fun getTopColorsFor(position: Int): Triple<Int, Int, Int>? = topColorCache[position]

    inner class VH(val binding: ItemReelBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemReelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    private fun sampleBottomLuminance(bmp: Bitmap): Double {
        val h = bmp.height
        val yStart = (h * 0.6).toInt().coerceAtMost(h - 1)
        val yEnd = h - 1
        var total = 0.0
        var count = 0
        val stepX = (bmp.width / 40).coerceAtLeast(1)
        val stepY = ((yEnd - yStart) / 20).coerceAtLeast(1)
        for (y in yStart until yEnd step stepY) {
            for (x in 0 until bmp.width step stepX) {
                val c = bmp.getPixel(x, y)
                val r = (c shr 16 and 0xFF)
                val g = (c shr 8 and 0xFF)
                val b = (c and 0xFF)
                // Perceptual luminance approximation
                val lum = 0.2126 * r + 0.7152 * g + 0.0722 * b
                total += lum
                count++
            }
        }
        return if (count == 0) 128.0 else total / count // 0..255 scale
    }

    private fun sampleTopBandColors(bmp: Bitmap, threshold: Int = 150): Triple<Int, Int, Int> {
        val w = bmp.width
        val h = bmp.height
        val bandEnd = (h * 0.12).toInt().coerceAtLeast(1) // top 12%
        val thirds = intArrayOf(0, w / 3, (2 * w) / 3, w)
        fun pickForRange(xs: Int, xe: Int): Int {
            val lums = ArrayList<Double>(200)
            val stepX = ((xe - xs) / 40).coerceAtLeast(1)
            val stepY = (bandEnd / 12).coerceAtLeast(1)
            var x = xs
            while (x < xe) {
                var y = 0
                while (y < bandEnd) {
                    val c = bmp.getPixel(x, y)
                    val r = (c shr 16) and 0xFF
                    val g = (c shr 8) and 0xFF
                    val b = c and 0xFF
                    lums.add(0.2126 * r + 0.7152 * g + 0.0722 * b)
                    y += stepY
                }
                x += stepX
            }
            if (lums.isEmpty()) return Color.WHITE
            lums.sort()
            val mean = lums.average()
            val p20 = lums[(lums.size * 0.2).toInt().coerceAtLeast(0).coerceAtMost(lums.size - 1)]
            // If there are dark patches, prefer white; if very bright overall, prefer black; otherwise pick max contrast
            return when {
                p20 < 130 -> Color.WHITE
                mean > 175 -> Color.BLACK
                else -> {
                    val whiteContrast = calculateContrast(Color.WHITE, approxBgFromLum(mean))
                    val blackContrast = calculateContrast(Color.BLACK, approxBgFromLum(mean))
                    if (whiteContrast >= blackContrast) Color.WHITE else Color.BLACK
                }
            }
        }
        val left = pickForRange(thirds[0], thirds[1])
        val center = pickForRange(thirds[1], thirds[2])
        val right = pickForRange(thirds[2], thirds[3])
        return Triple(left, center, right)
    }

    private fun approxBgFromLum(l: Double): Int {
        val v = l.coerceIn(0.0, 255.0).toInt()
        return Color.rgb(v, v, v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.reelBrand.text = item.brand
        holder.binding.reelName.text = item.name
        holder.binding.reelPrice.text = item.priceText
        val path = item.imageAssetPath

        // Check favorite status
        val favItem = FavoriteItem(
            id = item.id,
            name = item.name,
            desc = item.brand,
            price = item.priceText,
            imagePath = item.imageAssetPath
        )

        fun updateFavoriteIcon() {
            val isFav = FavoritesManager.isFavorite(favItem)
            holder.binding.reelFavorite.setImageResource(
                if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
            holder.binding.reelFavorite.setColorFilter(
                if (isFav) Color.parseColor("#E07A5F") else Color.WHITE
            )
        }

        updateFavoriteIcon()

        holder.binding.reelFavorite.setOnClickListener {
            if (FavoritesManager.isFavorite(favItem)) {
                FavoritesManager.removeFavorite(favItem)
            } else {
                FavoritesManager.addFavorite(favItem)
            }
            updateFavoriteIcon()
        }

        try {
            context.assets.open(path).use { input ->
                val bmp = BitmapFactory.decodeStream(input)
                holder.binding.reelImage.setImageBitmap(bmp)

                // Bottom text color & shadows
                val bottomLum = sampleBottomLuminance(bmp)
                val isBright = bottomLum > 150

                holder.binding.reelImage.setOnClickListener {
                    onItemClick?.invoke(item)
                }

                val chosenTextColor = if (isBright) Color.BLACK else Color.WHITE
                holder.binding.reelBrand.setTextColor(chosenTextColor)
                holder.binding.reelName.setTextColor(chosenTextColor)
                holder.binding.reelPrice.setTextColor(chosenTextColor)
                val shadowColor = if (isBright) 0x33000000 else 0x66000000
                holder.binding.reelBrand.setShadowLayer(6f, 0f, 2f, shadowColor)
                holder.binding.reelName.setShadowLayer(6f, 0f, 2f, shadowColor)
                holder.binding.reelPrice.setShadowLayer(6f, 0f, 2f, shadowColor)
                holder.binding.reelInfo.setBackgroundColor(Color.TRANSPARENT)

                // Per-label top colors
                val (leftC, centerC, rightC) = sampleTopBandColors(bmp)
                topColorCache[position] = Triple(leftC, centerC, rightC)
                onTopTextColorsSuggested?.invoke(leftC, centerC, rightC)

                // Palette accent for brand if safe
                Palette.from(bmp).clearFilters().generate { palette ->
                    palette?.vibrantSwatch?.let { swatch ->
                        val contrast = calculateContrast(swatch.rgb, if (isBright) Color.BLACK else Color.WHITE)
                        if (contrast > 3.0) holder.binding.reelBrand.setTextColor(swatch.rgb)
                    }
                }

                Log.d("ReelPagerAdapter", "Loaded asset: $path (${bmp.width}x${bmp.height})")
            }
        } catch (e: Exception) {
            Log.e("ReelPagerAdapter", "Failed to load asset: $path -> ${e.message}")
            holder.binding.reelImage.setImageResource(R.drawable.placeholder)
        }
    }

    private fun calculateContrast(fg: Int, bg: Int): Double {
        fun channel(v: Int): Double {
            val d = v / 255.0
            return if (d <= 0.03928) d / 12.92 else ((d + 0.055) / 1.055).pow(2.4)
        }

        fun relLum(c: Int): Double {
            val r = channel((c shr 16 and 0xFF))
            val g = channel((c shr 8 and 0xFF))
            val b = channel(c and 0xFF)
            return 0.2126 * r + 0.7152 * g + 0.0722 * b
        }

        val l1 = relLum(fg) + 0.05
        val l2 = relLum(bg) + 0.05
        return if (l1 > l2) l1 / l2 else l2 / l1
    }

    override fun getItemCount(): Int = items.size

    fun submit(list: List<ReelItem>) {
        items.clear()
        items.addAll(list)
        topColorCache.clear()
        notifyDataSetChanged()
    }
}
