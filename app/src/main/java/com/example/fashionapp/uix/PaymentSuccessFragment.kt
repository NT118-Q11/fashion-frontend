package com.example.fashionapp.uix

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ProductRatingAdapter
import com.example.fashionapp.adapter.ProductRatingItem
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityPaymentSuccessBinding
import com.example.fashionapp.model.RatingRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PaymentSuccessFragment : Fragment() {
    private var _binding: ActivityPaymentSuccessBinding? = null
    private val binding get() = _binding!!

    private var orderId: String? = null
    private lateinit var userManager: UserManager

    // Adapter for product ratings
    private var productRatingAdapter: ProductRatingAdapter? = null
    private var productRatingItems: MutableList<ProductRatingItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityPaymentSuccessBinding.inflate(inflater, container, false)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Get orderId and product rating items from arguments
        orderId = arguments?.getString("orderId")

        // Parse product rating items from JSON
        val productRatingItemsJson = arguments?.getString("productRatingItemsJson")
        if (!productRatingItemsJson.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<List<ProductRatingItem>>() {}.type
                val items: List<ProductRatingItem> = Gson().fromJson(productRatingItemsJson, type)
                productRatingItems = items.toMutableList()
            } catch (e: Exception) {
                Log.e("PaymentSuccessFragment", "Error parsing product rating items", e)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display order ID
        orderId?.let {
            binding.tvPaymentId.text = "Order ID: $it"
        }

        Log.d("PaymentSuccessFragment", "Received ${productRatingItems.size} products to rate")

        // Setup RecyclerView for product ratings
        setupProductRatingsRecyclerView()

        // Submit rating button
        binding.btnSubmit.setOnClickListener {
            submitRatings()
        }

        // Skip/Back to home button
        binding.btnBackHome.setOnClickListener {
            navigateToHome()
        }
    }

    private fun setupProductRatingsRecyclerView() {
        if (productRatingItems.isEmpty()) {
            binding.rvProductRatings.visibility = View.GONE
            binding.layoutEmpty.visibility = View.VISIBLE
            binding.tvRateTitle.visibility = View.GONE
            return
        }

        binding.rvProductRatings.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE

        productRatingAdapter = ProductRatingAdapter(productRatingItems)
        binding.rvProductRatings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = productRatingAdapter
        }
    }

    private fun submitRatings() {
        val ratedProducts = productRatingAdapter?.getRatedProducts() ?: emptyList()

        if (ratedProducts.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please rate at least one product",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val userId = userManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            navigateToHome()
            return
        }

        // Show loading
        binding.btnSubmit.isEnabled = false
        binding.btnSubmit.text = "Submitting..."

        lifecycleScope.launch {
            var successCount = 0
            var failCount = 0

            for (item in ratedProducts) {
                try {
                    val ratingRequest = RatingRequest(
                        productId = item.productId,
                        userId = userId,
                        rateStars = item.rating,
                        comment = item.comment.ifEmpty { null }
                    )

                    withContext(Dispatchers.IO) {
                        AppRoute.rating.createRating(ratingRequest)
                    }

                    successCount++
                    Log.d("PaymentSuccessFragment", "Rating submitted for product: ${item.productName}")

                } catch (e: Exception) {
                    failCount++
                    Log.e("PaymentSuccessFragment", "Error submitting rating for ${item.productName}", e)
                }
            }

            // Show result message
            val message = when {
                failCount == 0 -> "Thank you! All ${successCount} ratings submitted successfully!"
                successCount == 0 -> "Failed to submit ratings. Please try again later."
                else -> "Submitted $successCount rating(s). $failCount failed."
            }

            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()

            // Navigate to home
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        // Clear cart to ensure no leftover data
        val userId = userManager.getUserId()
        if (!userId.isNullOrEmpty()) {
            lifecycleScope.launch {
                try {
                    CartManager.clearCart(userId)
                    Log.d("PaymentSuccessFragment", "Cart cleared successfully")
                } catch (e: Exception) {
                    Log.e("PaymentSuccessFragment", "Error clearing cart", e)
                    // Continue navigation even if clear fails
                }

                // Navigate to home and clear back stack so user can't go back to payment screens
                findNavController().navigate(R.id.action_paymentSuccessFragment_to_homeFragment)
            }
        } else {
            // If no userId, just navigate
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_homeFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}