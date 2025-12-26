package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ItemForOverallRatingBinding
import com.example.fashionapp.model.RatingResponse
import com.example.fashionapp.uix.Details3Fragment

/**
 * Unified rating item that can be created from either API response or legacy Rating object
 */
data class RatingDisplayItem(
    val username: String,
    val stars: Int,
    val comment: String
) {
    companion object {
        fun fromResponse(response: RatingResponse, userName: String? = null): RatingDisplayItem {
            return RatingDisplayItem(
                username = userName ?: "User ${response.userId.takeLast(4)}",
                stars = response.rateStars,
                comment = response.comment ?: ""
            )
        }

        fun fromLegacy(rating: Details3Fragment.Rating): RatingDisplayItem {
            return RatingDisplayItem(
                username = rating.username,
                stars = rating.stars,
                comment = rating.comment
            )
        }
    }
}

class RatingAdapter(private var ratings: List<RatingDisplayItem>) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    // Secondary constructor for backward compatibility with legacy Rating
    constructor(legacyRatings: List<Details3Fragment.Rating>, dummy: Boolean = false) : this(
        legacyRatings.map { RatingDisplayItem.fromLegacy(it) }
    )

    inner class RatingViewHolder(val binding: ItemForOverallRatingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = ItemForOverallRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val currentRating = ratings[position]

        holder.binding.apply {
            tvUsername.text = currentRating.username
            tvComment.text = currentRating.comment

            val starImageViews = listOf(star1, star2, star3, star4, star5)

            // Cập nhật hình ảnh các ngôi sao dựa trên điểm đánh giá
            for (i in starImageViews.indices) {
                if (i < currentRating.stars) {
                    starImageViews[i].setImageResource(R.drawable.yellow_star)
                } else {
                    starImageViews[i].setImageResource(R.drawable.uncolor_star)
                }
            }
        }
    }

    override fun getItemCount(): Int = ratings.size

    /**
     * Update ratings with new data from API
     */
    fun updateRatings(newRatings: List<RatingDisplayItem>) {
        ratings = newRatings
        notifyDataSetChanged()
    }
}
