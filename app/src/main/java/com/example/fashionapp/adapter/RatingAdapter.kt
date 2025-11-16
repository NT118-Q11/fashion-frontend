package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ItemForOverallRatingBinding
import com.example.fashionapp.uix.Details3Fragment


class RatingAdapter(private val ratings: List<Details3Fragment.Rating>) : RecyclerView.Adapter<RatingAdapter.RatingViewHolder>() {

    // ViewHolder bây giờ sẽ sử dụng đúng lớp ItemForOverallRatingBinding.
    inner class RatingViewHolder(val binding: ItemForOverallRatingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        // Inflate đúng layout ItemForOverallRatingBinding.
        val binding = ItemForOverallRatingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        val currentRating = ratings[position]

        // Áp dụng logic trực tiếp trong onBindViewHolder để nhất quán và đơn giản hơn.
        holder.binding.apply {
            // Giả sử item_for_overall_rating.xml có các id là 'tvUsername', 'tvComment', và 'star1', 'star2', v.v.
            tvUsername.text = currentRating.username
            tvComment.text = currentRating.comment

            val starImageViews = listOf(
                star1,
                star2,
                star3,
                star4,
                star5
            )

            // Cập nhật hình ảnh các ngôi sao dựa trên điểm đánh giá.
            for (i in starImageViews.indices) {
                if (i < currentRating.stars) {
                    starImageViews[i].setImageResource(R.drawable.yellow_star)
                } else {
                    starImageViews[i].setImageResource(R.drawable.uncolor_star)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return ratings.size
    }
}
