package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.model.Product
import kotlinx.coroutines.launch

class ActivitySearchViewFragment : Fragment() {

    // Views
    private lateinit var recycler: RecyclerView
    private lateinit var paginationContainer: LinearLayout
    private lateinit var pagePrev: ImageView
    private lateinit var pageNext: ImageView
    private lateinit var tvResultCount: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var tvEmptyState: TextView
    private lateinit var searchInput: EditText

    // State
    private var currentPage = 1
    private val itemsPerPage = 4
    private var allProducts: List<Product> = emptyList()
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
        paginationContainer = view.findViewById(R.id.pagination_container)
        pagePrev = view.findViewById(R.id.page_prev)
        pageNext = view.findViewById(R.id.page_next)
        tvResultCount = view.findViewById(R.id.tv_result_count)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)
        searchInput = view.findViewById(R.id.search_input)

        // Setup RecyclerView
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        // Setup navigation listeners
        view.findViewById<View>(R.id.btn_back)?.setOnClickListener { findNavController().navigateUp() }
        view.findViewById<View>(R.id.btn_close)?.setOnClickListener {
            searchInput.text.clear()
            currentSearchKeyword = ""
            // Reload all products when clearing search
            loadProducts()
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
        searchInput.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }

        // Setup pagination listeners
        pagePrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePageUI()
            }
        }

        pageNext.setOnClickListener {
            val totalPages = getTotalPages()
            if (currentPage < totalPages) {
                currentPage++
                updatePageUI()
            }
        }

        // Load all products initially
        loadProducts()
    }

    private fun performSearch() {
        currentSearchKeyword = searchInput.text.toString().trim()
        currentPage = 1
        loadProducts()
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                showLoading(true)

                allProducts = if (currentSearchKeyword.isNotEmpty()) {
                    AppRoute.product.searchProducts(currentSearchKeyword)
                } else {
                    AppRoute.product.getAllProducts()
                }

                showLoading(false)

                // Show empty state only when search returns no results
                if (allProducts.isEmpty()) {
                    showEmptyState(true)
                } else {
                    showEmptyState(false)
                    updateResultCount()
                    updatePageUI()
                    updatePaginationButtons()
                }

            } catch (e: Exception) {
                showLoading(false)
                showEmptyState(true)
                e.printStackTrace()
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingIndicator.visibility = if (show) View.VISIBLE else View.GONE
        recycler.visibility = if (show) View.GONE else View.VISIBLE
        tvEmptyState.visibility = View.GONE
    }

    private fun showEmptyState(show: Boolean) {
        tvEmptyState.visibility = if (show) View.VISIBLE else View.GONE
        recycler.visibility = if (show) View.GONE else View.VISIBLE
        paginationContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun updateResultCount() {
        val count = allProducts.size
        val searchText = if (currentSearchKeyword.isNotEmpty()) {
            "\"$currentSearchKeyword\""
        } else {
            "ALL PRODUCTS"
        }
        tvResultCount.text = "$count RESULT${if (count != 1) "S" else ""} OF $searchText"
    }

    private fun getTotalPages(): Int {
        return if (allProducts.isEmpty()) 1 else (allProducts.size + itemsPerPage - 1) / itemsPerPage
    }

    private fun updatePaginationButtons() {
        // Clear existing page buttons (except prev/next arrows)
        val childCount = paginationContainer.childCount
        if (childCount > 2) {
            paginationContainer.removeViews(1, childCount - 2)
        }

        val totalPages = getTotalPages()

        if (totalPages <= 1) {
            paginationContainer.visibility = View.GONE
            return
        }

        paginationContainer.visibility = View.VISIBLE

        // Calculate which pages to show (max 7 buttons to prevent overflow)
        val pagesToShow = calculatePagesToShow(currentPage, totalPages)

        // Add page buttons dynamically
        var isFirstButton = true
        for (pageOrEllipsis in pagesToShow) {
            if (pageOrEllipsis == -1) {
                // Add ellipsis
                val ellipsisView = TextView(requireContext()).apply {
                    text = "..."
                    textSize = 16f
                    setPadding(16, 16, 16, 16)
                    setTextColor(Color.GRAY)
                    isClickable = false
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = if (isFirstButton) 0 else 16
                }

                paginationContainer.addView(ellipsisView, paginationContainer.childCount - 1, layoutParams)
            } else {
                // Add page button
                val page = pageOrEllipsis
                val pageButton = TextView(requireContext()).apply {
                    text = page.toString()
                    textSize = 16f
                    setPadding(24, 16, 24, 16)
                    setTextColor(if (page == currentPage) Color.WHITE else Color.BLACK)
                    setBackgroundResource(
                        if (page == currentPage) R.drawable.page_selected_bg
                        else R.drawable.page_unselected_bg
                    )
                    setOnClickListener {
                        currentPage = page
                        updatePageUI()
                        updatePaginationButtons()
                    }
                }

                val layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginStart = if (isFirstButton) 0 else 16
                }

                paginationContainer.addView(pageButton, paginationContainer.childCount - 1, layoutParams)
            }
            isFirstButton = false
        }

        // Update arrow states
        pagePrev.alpha = if (currentPage > 1) 1.0f else 0.3f
        pageNext.alpha = if (currentPage < totalPages) 1.0f else 0.3f
    }

    private fun calculatePagesToShow(current: Int, total: Int): List<Int> {
        if (total <= 7) {
            // Show all pages if total is 7 or less
            return (1..total).toList()
        }

        val pages = mutableListOf<Int>()

        // Always show first page
        pages.add(1)

        when {
            current <= 4 -> {
                // Near the beginning: 1 2 3 4 5 ... 10
                for (i in 2..minOf(5, total - 1)) {
                    pages.add(i)
                }
                if (total > 6) {
                    pages.add(-1) // ellipsis
                }
                pages.add(total)
            }
            current >= total - 3 -> {
                // Near the end: 1 ... 6 7 8 9 10
                pages.add(-1) // ellipsis
                for (i in maxOf(2, total - 4)..total) {
                    pages.add(i)
                }
            }
            else -> {
                // In the middle: 1 ... 4 5 6 ... 10
                pages.add(-1) // ellipsis
                for (i in current - 1..current + 1) {
                    pages.add(i)
                }
                pages.add(-1) // ellipsis
                pages.add(total)
            }
        }

        return pages
    }

    private fun updatePageUI() {
        // Calculate items for the current page
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allProducts.size)
        val pageItems = if (startIndex < allProducts.size) {
            allProducts.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        // Show empty state if no products
        if (pageItems.isEmpty()) {
            showEmptyState(true)
            return
        }

        showEmptyState(false)

        // Update RecyclerView with items for the current page
        recycler.adapter = SearchResultAdapter(pageItems) { product ->
            // Navigate to details with product ID
            // You can pass the product ID via arguments if needed
            val bundle = Bundle().apply {
                putString("productId", product.id)
            }
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_detailsFragment, bundle)
        }

        // Update pagination button states
        updatePaginationButtons()
    }

    private inner class SearchResultAdapter(
        private val products: List<Product>,
        private val onClick: (Product) -> Unit
    ) : RecyclerView.Adapter<SearchResultAdapter.VH>() {

        inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
            private val title: TextView = itemView.findViewById(R.id.txtTitle)
            private val price: TextView = itemView.findViewById(R.id.txtPrice)
            private val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)

            fun bind(product: Product) {
                title.text = product.name
                price.text = "$${String.format("%.2f", product.price)}"

                // Load product image from assets
                val assetPath = product.getThumbnailAssetPath()
                if (assetPath != null) {
                    try {
                        val inputStream = requireContext().assets.open(assetPath)
                        val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
                        imgProduct.setImageDrawable(drawable)
                        inputStream.close()
                    } catch (e: Exception) {
                        // Fallback to default image if asset not found
                        imgProduct.setImageResource(R.drawable.sample_woman)
                        e.printStackTrace()
                    }
                } else {
                    imgProduct.setImageResource(R.drawable.sample_woman)
                }

                // Handle favorite button (optional - implement favorite logic if needed)
                btnFavorite.setOnClickListener {
                    // TODO: Add to favorites
                }

                itemView.setOnClickListener { onClick(product) }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_result, parent, false)
            return VH(view)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(products[position])
        }

        override fun getItemCount(): Int = products.size
    }
}
