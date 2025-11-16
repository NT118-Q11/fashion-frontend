package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.adapter.RatingAdapter
import com.example.fashionapp.databinding.Details3Binding
class Details3Fragment : Fragment() { // Class bắt đầu ở đây

    data class Rating(
        val username: String,
        val stars: Int, // Số sao từ 1 đến 5
        val comment: String
    )

    private var _binding: Details3Binding? = null
    private val binding get() = _binding!!

    private lateinit var ratingAdapter: RatingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng ViewBinding để inflate layout
        _binding = Details3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingList = getSampleRatings()

        // Khởi tạo Adapter

        ratingAdapter = RatingAdapter(ratingList)


        binding.rvRatings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratingAdapter
        }

        // Cập nhật thông tin tổng quan (nếu cần)
        updateOverallRating(ratingList)
    }

    private fun updateOverallRating(ratings: List<Rating>) {
        val totalReviews = ratings.size
        binding.tvReviewCount.text = "Base on $totalReviews review"

        if (ratings.isNotEmpty()) {
            val averageRating = ratings.map { it.stars }.average()
        }
    }


    private fun getSampleRatings(): List<Rating> {
        return listOf(
            // SỬA LỖI: Sử dụng lớp `Rating` bạn đã định nghĩa, không phải từ thư viện media3
            Rating("Emelia Sans", 4, "Worth the money. Highly recommend. 10/10 for comfort and looks"),
            Rating("Jane Doe", 5, "Amazing product! I love it so much."),
            Rating("John Smith", 3, "It's okay, but I expected more for the price."),
            Rating("Alice Johnson", 5, "Perfect! Exactly what I was looking for.")
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}
