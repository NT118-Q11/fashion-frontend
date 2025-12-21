
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
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.data.CartItem
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.databinding.ProductDetailBinding
import com.example.fashionapp.model.Product
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!
    private var currentProduct: Product? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial image list for the main slider
        val initialImages = listOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        // Adapter for ViewPager2 (product images)
        binding.viewPagerProduct.adapter = ImageSliderAdapter(initialImages)
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()

        // Back button
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Tab navigation buttons
        binding.tabInfo.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.tabDescription.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details2Fragment)
        }
        binding.tabRating.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }

        // ADD TO CART button: load product details (if productId) or show default data,
        // and enable the add-to-cart action.
        binding.btnAddToCart.setOnClickListener {
            val productId = arguments?.getString("productId")
            if (productId != null) {
                loadProductDetails(productId)
            } else {
                setupDefaultData()
            }
        }

        // If there is a separate actionable add-to-cart control in layout, map it here.
        // Using btnAddToCart as the primary action to add item to cart.
        binding.btnAddToCart.setOnLongClickListener {
            // Example: long-press could directly add default/fallback item
            addToCart()
            true
        }
    }

    /**
     * Load product details from API
     */
    private fun loadProductDetails(productId: String) {
        lifecycleScope.launch {
            try {
                Log.d("DetailsFragment", "Loading product details for ID: $productId")

                // Show loading state (disable button while loading)
                binding.btnAddToCart.isEnabled = false

                currentProduct = AppRoute.product.getProductById(productId)

                Log.d("DetailsFragment", "Product loaded: ${currentProduct?.name}")

                // Update UI with product data
                setupProductUI(currentProduct!!)

                binding.btnAddToCart.isEnabled = true

            } catch (e: Exception) {
                Log.e("DetailsFragment", "Error loading product details", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to load product: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                // Show default data on error
                setupDefaultData()
                binding.btnAddToCart.isEnabled = true
            }
        }
    }

    /**
     * Setup UI with product data
     */
    private fun setupProductUI(product: Product) {
        // For now use drawable placeholders; extend to load actual product images later
        val imageList = mutableListOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        // TODO: if product.images or thumbnail are available, replace placeholders accordingly

        binding.viewPagerProduct.adapter = ImageSliderAdapter(imageList)
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()

        // You may pass product data to child fragments if needed
    }

    /**
     * Setup default/placeholder data
     */
    private fun setupDefaultData() {
        val imageList = listOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        binding.viewPagerProduct.adapter = ImageSliderAdapter(imageList)
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()
    }

    /**
     * Add current product to cart
     */
    private fun addToCart() {
        val product = currentProduct

        if (product != null) {
            val item = CartItem(
                id = product.id.hashCode(),
                title = product.brand ?: "Unknown Brand",
                description = product.name,
                price = product.price,
                imageRes = R.drawable.model_image_1,
                quantity = 1
            )

            CartManager.addItem(item)
            Toast.makeText(requireContext(), "Added ${product.name} to cart!", Toast.LENGTH_SHORT).show()
        } else {
            val item = CartItem(
                id = 1,
                title = "LAMEREI",
                description = "Recycle Boucle Knit Cardigan Pink",
                price = 120.0,
                imageRes = R.drawable.model_image_1,
                quantity = 1
            )

            CartManager.addItem(item)
            Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
