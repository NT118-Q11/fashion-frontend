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
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityPaymentSuccessBinding
import com.example.fashionapp.model.RatingRequest
import kotlinx.coroutines.launch


class PaymentSuccessFragment : Fragment() {
    private var _binding: ActivityPaymentSuccessBinding? = null
    private val binding get() = _binding!!

    private var orderId: String? = null
    private lateinit var userManager: UserManager

    // Store product IDs from the order to rate
    private var productIds: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityPaymentSuccessBinding.inflate(inflater, container, false)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Get orderId and productIds from arguments
        orderId = arguments?.getString("orderId")
        productIds = arguments?.getStringArray("productIds")?.toList() ?: emptyList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display order ID
        orderId?.let {
            binding.tvPaymentId.text = "Order ID: $it"
        }

        Log.d("PaymentSuccessFragment", "Received ${productIds.size} products to rate")

        // Set default rating to prevent crashes
        binding.ratingBar.rating = 0f

        // Fix EditText to prevent crashes
        binding.etComment.setText("")
        binding.etComment.hint = "Write your comment..."

        // Submit rating button
        binding.btnSubmit.setOnClickListener {
            submitRating()
        }

        // Back to home button
        binding.btnBackHome.setOnClickListener {
            navigateToHome()
        }
    }


    private fun submitRating() {
        val rating = binding.ratingBar.rating
        val comment = binding.etComment.text?.toString()?.trim() ?: ""

        // Validate rating
        if (rating == 0f) {
            Toast.makeText(requireContext(), "Please select a rating", Toast.LENGTH_SHORT).show()
            return
        }

        if (productIds.isEmpty()) {
            Toast.makeText(requireContext(), "No products found to rate", Toast.LENGTH_SHORT).show()
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
            try {
                // Submit rating for the first product (or all products if you want)
                // For simplicity, we'll rate the first product in the order
                val productId = productIds.firstOrNull()

                if (productId.isNullOrEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        "No product found to rate",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.btnSubmit.isEnabled = true
                    binding.btnSubmit.text = "SUBMIT"
                    return@launch
                }

                val ratingRequest = RatingRequest(
                    productId = productId,
                    userId = userId,
                    rateStars = rating.toInt(),
                    comment = comment.ifEmpty { null }
                )

                AppRoute.rating.createRating(ratingRequest)

                Toast.makeText(
                    requireContext(),
                    "Thank you for your feedback!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate to home after successful submission
                navigateToHome()

            } catch (e: Exception) {
                Log.e("PaymentSuccessFragment", "Error submitting rating", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to submit rating. Please try again later.",
                    Toast.LENGTH_LONG
                ).show()

                // Enable button again
                binding.btnSubmit.isEnabled = true
                binding.btnSubmit.text = "SUBMIT"
            }
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