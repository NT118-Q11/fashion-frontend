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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.adapter.RatingAdapter
import com.example.fashionapp.adapter.RatingDisplayItem
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ProductDetailBinding
import com.example.fashionapp.model.Product
import com.example.fashionapp.model.ProductInformation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class DetailsFragment : Fragment() {

    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!
    
    private var productId: String? = null
    private var currentProduct: Product? = null
    private lateinit var userManager: UserManager

    private var selectedColor: String? = null
    private var selectedSize: String? = null

    // Tab selection state
    private var isInfoTabSelected: Boolean = true

    // Bottom Sheet
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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

        // Setup Bottom Sheet
        setupBottomSheet()

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

        // 3 nút chuyển tab (in-place)
        binding.tabInfo.setOnClickListener {
            selectInfoTab()
            // Expand bottom sheet to show content
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.tabRating.setOnClickListener {
            selectRatingTab()
            // Expand bottom sheet to show content
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        // Setup ratings RecyclerView
        setupRatings()

        // NÚT ADD TO CART
        binding.btnAddToCart.setOnClickListener {
            addToCart()
        }
    }

    private fun loadProductDetails(id: String) {
        lifecycleScope.launch {
            try {
                val product = withContext(Dispatchers.IO) {
                    AppRoute.product.getProductById(id)
                }
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

        // Update information tab content
        updateProductInfo(product)
    }

    private fun setupBottomSheet() {
        val bottomSheet = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)

        // Set initial state
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = dpToPx(280)
        bottomSheetBehavior.isHideable = false

        // Allow expanding to full screen
        bottomSheetBehavior.isFitToContents = false
        bottomSheetBehavior.halfExpandedRatio = 0.6f  // 60% of screen when half expanded
        bottomSheetBehavior.expandedOffset = dpToPx(50)  // Leave 50dp from top when fully expanded

        // Add callback for state changes
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        Log.d("DetailsFragment", "Bottom sheet expanded")
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        Log.d("DetailsFragment", "Bottom sheet collapsed")
                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        Log.d("DetailsFragment", "Bottom sheet half expanded")
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        Log.d("DetailsFragment", "Bottom sheet dragging")
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Optional: add visual effects during sliding
                // slideOffset: -1 = hidden, 0 = collapsed, 1 = expanded
            }
        })

        // Allow drag handle to expand/collapse
        binding.dragHandle.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                }
                BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }
                else -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
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

    private fun selectInfoTab() {
        isInfoTabSelected = true

        // Update tab appearance
        binding.tabInfo.setTextColor(resources.getColor(R.color.black, null))
        binding.tabRating.setTextColor(resources.getColor(R.color.noive_text_secondary, null))

        // Show/hide content
        binding.infoContent.visibility = View.VISIBLE
        binding.ratingContent.visibility = View.GONE
    }

    private fun selectRatingTab() {
        isInfoTabSelected = false

        // Update tab appearance
        binding.tabInfo.setTextColor(resources.getColor(R.color.noive_text_secondary, null))
        binding.tabRating.setTextColor(resources.getColor(R.color.black, null))

        // Show/hide content
        binding.infoContent.visibility = View.GONE
        binding.ratingContent.visibility = View.VISIBLE
    }

    private var ratingAdapter: RatingAdapter? = null

    private fun setupRatings() {
        // Initialize with empty list, will be populated from API
        ratingAdapter = RatingAdapter(emptyList<RatingDisplayItem>())

        binding.rvRatings.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ratingAdapter
            isNestedScrollingEnabled = false
        }

        // Show loading state
        binding.tvReviewCount.text = "Loading reviews..."

        // Load ratings from API when product is available
        productId?.let { loadRatingsFromApi(it) }
    }

    /**
     * Load ratings from backend API for the current product
     */
    private fun loadRatingsFromApi(productId: String) {
        lifecycleScope.launch {
            try {
                val ratings = withContext(Dispatchers.IO) {
                    AppRoute.rating.getRatingsByProduct(productId)
                }

                if (ratings.isNotEmpty()) {
                    // Convert API responses to display items
                    val displayItems = ratings.map { rating ->
                        RatingDisplayItem.fromResponse(rating)
                    }

                    ratingAdapter?.updateRatings(displayItems)
                    binding.tvReviewCount.text = "Based on ${ratings.size} reviews"

                    // Update stars based on average rating
                    val averageRating = ratings.map { it.rateStars }.average()
                    updateStars(averageRating)

                    Log.d("DetailsFragment", "Loaded ${ratings.size} ratings from API")
                } else {
                    // No ratings yet, show 0 stars and 0 reviews
                    displayNoRatings()
                }
            } catch (e: Exception) {
                Log.w("DetailsFragment", "Failed to load ratings from API", e)
                // On error, show 0 ratings
                displayNoRatings()
            }
        }
    }

    /**
     * Display no ratings state - 0 stars and 0 reviews
     */
    private fun displayNoRatings() {
        ratingAdapter?.updateRatings(emptyList())
        binding.tvReviewCount.text = "No reviews yet"
        updateStars(0.0)
    }

    private fun updateStars(rating: Double) {
        val starViews = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)

        starViews.forEachIndexed { index, starView ->
            if (index < rating.toInt()) {
                starView.setImageResource(R.drawable.yellow_star)
            } else {
                starView.setImageResource(R.drawable.uncolor_star)
            }
        }
    }


    private fun updateProductInfo(product: Product) {
        // Update information tab content with product details
        binding.tvProductType.text = product.category?.uppercase() ?: "PRODUCT"

        // Load product information from API
        loadProductInformation(product.id)
    }

    /**
     * Load product information from backend API
     */
    private fun loadProductInformation(productId: String) {
        lifecycleScope.launch {
            try {
                val productInfo = withContext(Dispatchers.IO) {
                    AppRoute.productInformation.getProductInformationByProductId(productId)
                }
                // Update UI with product information from API
                displayProductInformation(productInfo)
                Log.d("DetailsFragment", "Product information loaded successfully for product: $productId")
            } catch (e: Exception) {
                Log.w("DetailsFragment", "Product information not found for product: $productId, using fallback", e)
                // Fallback to basic product info if API fails
                displayFallbackProductInfo()
            }
        }
    }

    /**
     * Display product information from API response
     */
    private fun displayProductInformation(productInfo: ProductInformation) {
        binding.tvProductType.text = productInfo.category.uppercase()
        binding.tvInfoContent.text = productInfo.toDisplayText()
    }

    /**
     * Display fallback product information when API fails
     */
    private fun displayFallbackProductInfo() {
        val product = currentProduct
        val infoText = buildString {
            append("• Brand: ${product?.brand ?: "Premium"}\n")
            append("• Category: ${product?.category ?: "Fashion"}\n")
            append("• Gender: ${product?.gender ?: "Unisex"}\n")
            append("• Fit: Regular fit\n")
            append("• Care: Machine washable\n")
            append("• Origin: Made in Vietnam")
        }
        binding.tvInfoContent.text = infoText
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
            val success = CartManager.addToCart(uid, pid, 1, selectedSize, selectedColor)
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
