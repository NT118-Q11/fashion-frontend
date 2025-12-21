package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ProductAdapter
import com.example.fashionapp.model.Product
import kotlinx.coroutines.launch

class ActivitySearchViewFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var searchInput: EditText
    private lateinit var tvResultCount: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var loadingIndicator: View
    private lateinit var paginationContainer: LinearLayout
    private lateinit var pagePrev: ImageView
    private lateinit var pageNext: ImageView
    private lateinit var productAdapter: ProductAdapter

    private var currentPage = 1
    private val itemsPerPage = 4
    private var allProducts: List<Product> = emptyList()
    private var filteredProducts: List<Product> = emptyList()
    private var currentSearchKeyword: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        recycler = view.findViewById(R.id.rcv_search_results)
        searchInput = view.findViewById(R.id.search_input)
        tvResultCount = view.findViewById(R.id.tv_result_count)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        paginationContainer = view.findViewById(R.id.pagination_container)
        pagePrev = view.findViewById(R.id.page_prev)
        pageNext = view.findViewById(R.id.page_next)

        // Setup RecyclerView
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        productAdapter = ProductAdapter(emptyList()) { product ->
            // Handle product click - navigate to details with product ID
            val bundle = Bundle().apply {
                putString("productId", product.id)
            }
            findNavController().navigate(
                R.id.action_activitySearchViewFragment_to_detailsFragment,
                bundle
            )
        }
        recycler.adapter = productAdapter

        // Setup navigation listeners
        view.findViewById<View>(R.id.btn_back)?.setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<View>(R.id.btn_close)?.setOnClickListener {
            searchInput.text.clear()
            currentSearchKeyword = ""
            loadAllProducts()
        }
        view.findViewById<View>(R.id.btn_filter)?.setOnClickListener {
            // Trigger search
            performSearch()
        }
        view.findViewById<View>(R.id.navHome)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_homeFragment)
        }
        view.findViewById<View>(R.id.navProfile)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_myAccountFragment)
        }
        view.findViewById<View>(R.id.navCart)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_cartFragment)
        }
        view.findViewById<View>(R.id.navNotifications)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_notificationFragment)
        }

        // Setup search input listener
        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                // Auto-search when user stops typing (you can add debounce here)
                currentSearchKeyword = s?.toString() ?: ""
            }
        })

        // Setup pagination listeners
        pagePrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updateUI()
            }
        }

        pageNext.setOnClickListener {
            val totalPages = getTotalPages()
            if (currentPage < totalPages) {
                currentPage++
                updateUI()
            }
        }

        // Load initial data
        loadAllProducts()
    }

    /**
     * Load all products from API
     */
    private fun loadAllProducts() {
        lifecycleScope.launch {
            try {
                showLoading(true)
                Log.d("SearchView", "Loading all products...")
                allProducts = AppRoute.product.getAllProducts()
                filteredProducts = allProducts
                currentPage = 1
                Log.d("SearchView", "Loaded ${allProducts.size} products")
                showLoading(false)
                updateUI()
            } catch (e: Exception) {
                Log.e("SearchView", "Error loading products", e)
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Failed to load products: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                // Show empty state
                allProducts = emptyList()
                filteredProducts = emptyList()
                updateUI()
            }
        }
    }

    /**
     * Perform search based on keyword
     */
    private fun performSearch() {
        val keyword = searchInput.text.toString().trim()
        if (keyword.isEmpty()) {
            loadAllProducts()
            return
        }

        lifecycleScope.launch {
            try {
                showLoading(true)
                Log.d("SearchView", "Searching for: $keyword")
                filteredProducts = AppRoute.product.searchProducts(keyword)
                currentPage = 1
                Log.d("SearchView", "Found ${filteredProducts.size} products")
                showLoading(false)
                updateUI()
            } catch (e: Exception) {
                Log.e("SearchView", "Error searching products", e)
                showLoading(false)
                Toast.makeText(
                    requireContext(),
                    "Search failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Calculate total number of pages
     */
    private fun getTotalPages(): Int {
        return if (filteredProducts.isEmpty()) 1
        else (filteredProducts.size + itemsPerPage - 1) / itemsPerPage
    }

    /**
     * Get products for current page
     */
    private fun getCurrentPageProducts(): List<Product> {
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, filteredProducts.size)
        return if (startIndex < filteredProducts.size) {
            filteredProducts.subList(startIndex, endIndex)
        } else {
            emptyList()
        }
    }

    /**
     * Update UI with current page data
     */
    private fun updateUI() {
        // Update result count
        val searchText = if (currentSearchKeyword.isNotEmpty()) {
            currentSearchKeyword.uppercase()
        } else {
            "ALL PRODUCTS"
        }
        tvResultCount.text = "${filteredProducts.size} RESULT${if (filteredProducts.size != 1) "S" else ""} OF $searchText"

        // Update RecyclerView
        val pageProducts = getCurrentPageProducts()
        productAdapter.updateProducts(pageProducts)

        // Show/hide empty state
        if (filteredProducts.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            paginationContainer.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            paginationContainer.visibility = View.VISIBLE
            // Update pagination
            updatePagination()
        }
    }

    /**
     * Show or hide loading indicator
     */
    private fun showLoading(isLoading: Boolean) {
        loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
        recycler.visibility = if (isLoading) View.GONE else View.VISIBLE
        paginationContainer.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    /**
     * Update pagination UI dynamically
     */
    private fun updatePagination() {
        val totalPages = getTotalPages()

        // Clear existing page buttons
        paginationContainer.removeAllViews()

        // Re-add prev button
        paginationContainer.addView(pagePrev)

        // Calculate page range to display
        val maxPagesToShow = 5
        val startPage: Int
        val endPage: Int

        when {
            totalPages <= maxPagesToShow -> {
                // Show all pages
                startPage = 1
                endPage = totalPages
            }
            currentPage <= 3 -> {
                // Near the beginning
                startPage = 1
                endPage = maxPagesToShow
            }
            currentPage >= totalPages - 2 -> {
                // Near the end
                startPage = totalPages - maxPagesToShow + 1
                endPage = totalPages
            }
            else -> {
                // In the middle
                startPage = currentPage - 2
                endPage = currentPage + 2
            }
        }

        // Create page buttons
        for (page in startPage..endPage) {
            val pageButton = createPageButton(page, page == currentPage)
            paginationContainer.addView(pageButton)
        }

        // Re-add next button
        paginationContainer.addView(pageNext)

        // Enable/disable navigation arrows
        pagePrev.alpha = if (currentPage > 1) 1.0f else 0.3f
        pagePrev.isEnabled = currentPage > 1

        pageNext.alpha = if (currentPage < totalPages) 1.0f else 0.3f
        pageNext.isEnabled = currentPage < totalPages
    }

    /**
     * Create a page button dynamically
     */
    private fun createPageButton(pageNumber: Int, isSelected: Boolean): TextView {
        val buttonSizePx = (32 * resources.displayMetrics.density).toInt()
        val marginStartPx = (8 * resources.displayMetrics.density).toInt()
        val marginBetweenPx = (6 * resources.displayMetrics.density).toInt()

        val pageButton = TextView(requireContext())
        pageButton.apply {
            text = pageNumber.toString()
            width = buttonSizePx
            height = buttonSizePx
            gravity = android.view.Gravity.CENTER
            textSize = 14f

            setBackgroundResource(
                if (isSelected) R.drawable.page_selected_bg
                else R.drawable.page_unselected_bg
            )
            setTextColor(if (isSelected) Color.WHITE else Color.BLACK)

            // Set margins
            val params = LinearLayout.LayoutParams(buttonSizePx, buttonSizePx)
            params.marginStart = if (pageNumber == 1) marginStartPx else marginBetweenPx
            layoutParams = params

            // Set click listener
            setOnClickListener {
                currentPage = pageNumber
                updateUI()
            }
        }
        return pageButton
    }
}
