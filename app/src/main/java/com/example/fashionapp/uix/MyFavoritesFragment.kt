package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.FavoritesAdapter
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.databinding.ActivityMyFavoritesBinding
import com.example.fashionapp.model.FavoriteItem
import kotlinx.coroutines.launch

class MyFavoritesFragment : Fragment() {
    private var _binding: ActivityMyFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var pagePrev: ImageView
    private lateinit var pageNext: ImageView
    private lateinit var favoritesManager: FavoritesManager

    private var currentPage = 1
    private val itemsPerPage = 4
    private var allItems: List<FavoriteItem> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMyFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        favoritesManager = FavoritesManager.getInstance(requireContext())
        loadFavorites()

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)

        pagePrev = binding.pagePrev
        pageNext = binding.pageNext

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

        updatePageUI()

        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_MyFavoritesFragment_to_myAccountFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_MyFavoritesFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_MyFavoritesFragment_to_notificationFragment)
        }

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_MyFavoritesFragment_to_homeFragment)
        }
    }

    private fun loadFavorites() {
        // Show loading state
        binding.rvFavorites.visibility = View.GONE

        // Reload favorites from server
        favoritesManager.reloadFavorites { success ->
            // Hide loading and show content
            binding.rvFavorites.visibility = View.VISIBLE

            if (success) {
                allItems = favoritesManager.getFavorites()
                // Reset current page if it exceeds total pages after deletion
                val totalPages = getTotalPages()
                if (currentPage > totalPages && currentPage > 1) {
                    currentPage = totalPages
                }
                if (allItems.isEmpty()) {
                    currentPage = 1
                }
                updatePageUI()
            }
        }
    }

    private fun getTotalPages(): Int {
        return if (allItems.isEmpty()) 1 else (allItems.size + itemsPerPage - 1) / itemsPerPage
    }

    private fun updatePaginationButtons() {
        val paginationContainer = binding.paginationContainer

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
        val totalPagesCount = getTotalPages()
        pagePrev.alpha = if (currentPage > 1) 1.0f else 0.3f
        pageNext.alpha = if (currentPage < totalPagesCount) 1.0f else 0.3f
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
        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allItems.size)

        val pageItems = if (startIndex < allItems.size) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        binding.rvFavorites.adapter = FavoritesAdapter(pageItems) { itemToRemove ->
            favoritesManager.removeFavorite(itemToRemove) { success ->
                if (success) {
                    loadFavorites()
                    updatePageUI()
                }
            }
        }

        // Update pagination buttons
        updatePaginationButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}