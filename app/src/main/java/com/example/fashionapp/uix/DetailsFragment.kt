package com.example.fashionapp.uix

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

    private var selectedColor: String? = null
    private var selectedSize: String? = null

    // Fixed available sizes
    private val ALL_SIZES = listOf("S", "M", "L", "XL", "XXL")

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
        // Reset selection state for new product
        selectedColor = null
        selectedSize = null

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

            // Setup colors and sizes
            setupColors(product)
            setupSizes(product)
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

    private fun setupColors(product: Product) {
        // Parse colors from product
        val availableColors = product.colors.orEmpty()

        if (availableColors.isEmpty()) {
            // Hide entire color container if no colors
            binding.colorContainer.visibility = View.GONE
            return
        }

        // Show color container
        binding.colorContainer.visibility = View.VISIBLE

        // List of color views (ShapeableImageView)
        val colorViews = listOf(binding.color1, binding.color2, binding.color3)

        // Limit to maximum 3 colors
        val colorsToShow = availableColors.take(3)

        // Set first color as default selected
        if (selectedColor == null && colorsToShow.isNotEmpty()) {
            selectedColor = colorsToShow[0]
        }

        // Setup each color view
        colorsToShow.forEachIndexed { index, colorName ->
            if (index < colorViews.size) {
                val colorView = colorViews[index]
                colorView.visibility = View.VISIBLE

                // Set the actual color as background
                val colorInt = parseColorName(colorName)
                colorView.setBackgroundColor(colorInt)

                // Store color name in tag for selection tracking
                colorView.tag = colorName

                colorView.setOnClickListener {
                    selectedColor = colorName
                    updateColorSelection(colorsToShow)
                }
            }
        }

        // Hide unused color views
        for (i in colorsToShow.size until colorViews.size) {
            colorViews[i].visibility = View.GONE
        }

        updateColorSelection(colorsToShow)
    }

    private fun setupSizes(product: Product) {
        // Parse sizes from product (already normalized to uppercase)
        val availableSizes = product.sizes.orEmpty()

        // Set first available size as default selected
        if (selectedSize == null && availableSizes.isNotEmpty()) {
            selectedSize = availableSizes[0]
        }

        // Setup Spinner with available sizes
        if (availableSizes.isNotEmpty()) {
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_size_item,
                availableSizes
            )
            adapter.setDropDownViewResource(R.layout.spinner_size_dropdown_item)
            binding.spinnerSize.adapter = adapter

            // Set default selection
            if (selectedSize != null && availableSizes.contains(selectedSize)) {
                binding.spinnerSize.setSelection(availableSizes.indexOf(selectedSize))
            }

            // Handle size selection
            binding.spinnerSize.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    selectedSize = availableSizes[position]
                    Log.d("DetailsFragment", "Size selected: $selectedSize")
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }
            }
        } else {
            // No sizes available - hide the size selector
            binding.sizeContainer.visibility = View.GONE
        }
    }

    private fun updateColorSelection(availableColors: List<String>) {
        // Update all color views to show selection state
        val colorViews = listOf(binding.color1, binding.color2, binding.color3)

        colorViews.forEach { colorView ->
            if (colorView.visibility == View.VISIBLE) {
                val isSelected = colorView.tag == selectedColor

                // Update selection indicator using stroke (like details_1)
                if (isSelected) {
                    // Selected: show black stroke border
                    colorView.strokeWidth = dpToPx(2).toFloat()
                    colorView.strokeColor = resources.getColorStateList(R.color.black, null)
                    colorView.elevation = dpToPx(2).toFloat()
                } else {
                    // Unselected: no stroke
                    colorView.strokeWidth = 0f
                    colorView.elevation = 0f
                }
            }
        }
    }


    private fun parseColorName(colorName: String): Int {
        // Map color names to actual color values
        return when (colorName.lowercase().trim()) {
            "black" -> resources.getColor(R.color.black, null)
            "white" -> resources.getColor(R.color.white, null)
            "red" -> android.graphics.Color.RED
            "blue" -> android.graphics.Color.BLUE
            "green" -> android.graphics.Color.GREEN
            "yellow" -> android.graphics.Color.YELLOW
            "orange" -> resources.getColor(R.color.orange, null)
            "brown" -> resources.getColor(R.color.accent_brown, null)
            "gray", "grey" -> resources.getColor(R.color.gray, null)
            "beige" -> resources.getColor(R.color.vintage_beige, null)
            "pink" -> android.graphics.Color.parseColor("#FFC0CB")
            "purple" -> resources.getColor(R.color.purple_500, null)
            // Additional brand colors
            "khaki" -> android.graphics.Color.parseColor("#C3B091")
            "ivory" -> android.graphics.Color.parseColor("#FFFFF0")
            "navy" -> android.graphics.Color.parseColor("#000080")
            "maroon" -> android.graphics.Color.parseColor("#800000")
            "olive" -> android.graphics.Color.parseColor("#808000")
            "teal" -> android.graphics.Color.parseColor("#008080")
            "silver" -> android.graphics.Color.parseColor("#C0C0C0")
            "gold" -> android.graphics.Color.parseColor("#FFD700")
            "cream" -> android.graphics.Color.parseColor("#FFFDD0")
            "charcoal" -> android.graphics.Color.parseColor("#36454F")
            else -> {
                // Try to parse as hex color
                try {
                    android.graphics.Color.parseColor(colorName)
                } catch (e: Exception) {
                    // Default to a medium gray if color not recognized
                    android.graphics.Color.parseColor("#888888")
                }
            }
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    /**
     * Get the currently selected color
     */
    fun getSelectedColor(): String? = selectedColor

    /**
     * Get the currently selected size
     */
    fun getSelectedSize(): String? = selectedSize

    private fun addToCart() {
        val uid = userManager.getUserId()
        val pid = currentProduct?.id ?: productId
        val product = currentProduct

        if (uid == null) {
            Toast.makeText(context, "Please login first", Toast.LENGTH_SHORT).show()
            return
        }
        
        if (pid == null) {
            Toast.makeText(context, "Product data not loaded", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate size selection if product has sizes
        if (!product?.sizes.isNullOrEmpty() && selectedSize == null) {
            Toast.makeText(context, "Please select a size", Toast.LENGTH_SHORT).show()
            return
        }

        // Log selected variant for debugging
        Log.d("DetailsFragment", "Adding to cart - Color: $selectedColor, Size: $selectedSize")

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
