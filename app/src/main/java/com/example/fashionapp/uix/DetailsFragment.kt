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
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ProductDetailBinding
import com.example.fashionapp.model.Product
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlin.math.abs

class DetailsFragment : Fragment() {

    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!
    
    private var productId: String? = null
    private var currentProduct: Product? = null
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductDetailBinding.inflate(inflater, container, false)
        userManager = UserManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productId = arguments?.getString("productId")
        Log.d("DetailsFragment", "Current Product ID: $productId")

        if (productId != null) {
            loadProductDetails(productId!!)
        } else {
            fetchDemoProduct()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Navigation buttons
        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_activitySearchViewFragment)
        }

        binding.btnCart.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_cartFragment)
        }

        // 3 nút chuyển fragment
        binding.tabInfo.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.tabRating.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }

        // NÚT ADD TO CART
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun loadProductDetails(id: String) {
        lifecycleScope.launch {
            try {
                val product = AppRoute.product.getProductById(id)
                currentProduct = product
                updateUI(product)
            } catch (e: Exception) {
                Log.e("DetailsFragment", "Error loading product: $id", e)
                Toast.makeText(context, "Product not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchDemoProduct() {
        loadProductDetails("6949194c50f862c6c91de7a") 
    }

    private fun updateUI(product: Product) {
        binding.apply {
            tvName.text = product.name
            tvDescription.text = product.description
            tvPrice.text = "$${product.price}"
            btnAddToCart.text = "Add To Cart · $${product.price}"
            
            // Get all product images from assets
            val productImages = product.getImageAssetPaths(requireContext().assets)

            if (productImages.isNotEmpty()) {
                // Use real product images
                Log.d("DetailsFragment", "Loading ${productImages.size} images for product: ${product.name}")
                productImages.forEach { imagePath ->
                    Log.d("DetailsFragment", "  - Image: $imagePath")
                }
                viewPagerProduct.adapter = ImageSliderAdapter(productImages, requireContext())
            } else {
                // Fallback to placeholder images if no images found
                Log.w("DetailsFragment", "No images found for product: ${product.name}, using placeholders")
                val placeholderList = listOf(
                    R.drawable.model_image_1,
                    R.drawable.model_image_2,
                    R.drawable.model_image_3
                )
                viewPagerProduct.adapter = ImageSliderAdapter(placeholderList)
            }

            // Setup ViewPager2 for better display
            setupImageSlider()

            TabLayoutMediator(tabLayoutProduct, viewPagerProduct) { _, _ -> }.attach()
        }
    }

    private fun setupImageSlider() {
        binding.viewPagerProduct.apply {
            // Set offscreen page limit
            offscreenPageLimit = 1

            // Remove over-scroll effect
            (getChildAt(0) as? RecyclerView)?.overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            // Create composite page transformer
            val compositePageTransformer = CompositePageTransformer()

            // Add margin between pages
            compositePageTransformer.addTransformer(MarginPageTransformer(16))

            // Add scale and alpha transformation
            compositePageTransformer.addTransformer { page, position ->
                val absPosition = abs(position)
                page.apply {
                    when {
                        // Current page (centered)
                        absPosition < 0.5f -> {
                            scaleY = 1.0f - (absPosition * 0.1f)
                            alpha = 1.0f
                        }
                        // Adjacent pages
                        absPosition < 1.0f -> {
                            scaleY = 0.95f - ((absPosition - 0.5f) * 0.1f)
                            alpha = 0.5f + (1.0f - absPosition) * 0.5f
                        }
                        // Other pages
                        else -> {
                            scaleY = 0.85f
                            alpha = 0.3f
                        }
                    }
                }
            }

            setPageTransformer(compositePageTransformer)
        }
    }

    private fun addToCart() {
        val uid = userManager.getUserId()
        val pid = currentProduct?.id ?: productId

        if (uid == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (pid == null) {
            Toast.makeText(context, "Product data not loaded", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            binding.btnAddToCart.isEnabled = false
            val success = CartManager.addToCart(uid, pid, 1)
            binding.btnAddToCart.isEnabled = true
            
            if (success) {
                Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
