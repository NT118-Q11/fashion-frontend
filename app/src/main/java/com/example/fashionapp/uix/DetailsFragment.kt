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

        // Setup back button
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Setup tab navigation buttons (match IDs from product_detail.xml)
        binding.tabInfo.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.tabRating.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }

        // Load product data if productId is provided
        val productId = arguments?.getString("productId")
        if (productId != null) {
            loadProductDetails(productId)
        } else {
            // Show default/placeholder data
            setupDefaultData()
        }

        // Setup add to cart button
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    /**
     * Load product details from API
     */
    private fun loadProductDetails(productId: String) {
        lifecycleScope.launch {
            try {
                Log.d("DetailsFragment", "Loading product details for ID: $productId")

                // Show loading state (optional - you can add a progress bar to layout)
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
        // Setup image slider
        val imageList = mutableListOf<Any>()

        // Add thumbnail from assets if available
        val thumbnailPath = product.getThumbnailAssetPath()
        if (!thumbnailPath.isNullOrEmpty()) {
            imageList.add(thumbnailPath)
            Log.d("DetailsFragment", "Loading thumbnail from assets: $thumbnailPath")
        }

        // Add additional images from product.images if available
        // TODO: Handle product.images when they are provided

        // If no images available, use placeholder drawables
        if (imageList.isEmpty()) {
            imageList.addAll(listOf(
                R.drawable.model_image_1,
                R.drawable.model_image_2,
                R.drawable.model_image_3
            ))
        }

        binding.viewPagerProduct.adapter = ImageSliderAdapter(imageList, requireContext())
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()

        // Note: Product name, price, and other details are shown in Details1Fragment (Information tab)
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

        binding.viewPagerProduct.adapter = ImageSliderAdapter(imageList, requireContext())
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()
    }

    /**
     * Add current product to cart
     */
    private fun addToCart() {
        val product = currentProduct

        if (product != null) {
            val item = CartItem(
                id = product.id.hashCode(), // Convert string ID to int for CartItem
                title = product.brand ?: "Unknown Brand",
                description = product.name,
                price = product.price,
                imageRes = R.drawable.model_image_1, // Use placeholder for now
                quantity = 1
            )

            CartManager.addItem(item)
            Toast.makeText(requireContext(), "Added ${product.name} to cart!", Toast.LENGTH_SHORT).show()
        } else {
            // Fallback to default item
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
