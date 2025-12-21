package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.FavoritesAdapter
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.databinding.ActivityMyFavoritesBinding
import com.example.fashionapp.model.FavoriteItem

class MyFavoritesFragment : Fragment() {
    private var _binding: ActivityMyFavoritesBinding? = null
    private val binding get() = _binding!!

    private lateinit var pageButtons: List<TextView>
    private lateinit var pagePrev: ImageView
    private lateinit var pageNext: ImageView

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

        loadFavorites()

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)

        pageButtons = listOf(
            binding.page1,
            binding.page2,
            binding.page3,
            binding.page4,
            binding.page5
        )
        pagePrev = binding.pagePrev
        pageNext = binding.pageNext

        pagePrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePageUI()
            }
        }

        pageNext.setOnClickListener {
            if (currentPage < pageButtons.size) {
                currentPage++
                updatePageUI()
            }
        }

        pageButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                currentPage = index + 1
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
        allItems = FavoritesManager.getFavorites()
        // Reset current page if it exceeds total pages after deletion
        val totalPages = (allItems.size + itemsPerPage - 1) / itemsPerPage
        if (currentPage > totalPages && currentPage > 1) {
            currentPage = totalPages
        }
        if (allItems.isEmpty()) {
            currentPage = 1
        }
    }

    private fun updatePageUI() {
        pageButtons.forEachIndexed { index, button ->
            val isSelected = (index + 1) == currentPage
            button.setBackgroundResource(
                if (isSelected) R.drawable.page_selected_bg else R.drawable.page_unselected_bg
            )
            button.setTextColor(
                if (isSelected) Color.WHITE else Color.BLACK
            )
        }

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, allItems.size)

        val pageItems = if (startIndex < allItems.size) {
            allItems.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        binding.rvFavorites.adapter = FavoritesAdapter(pageItems) { itemToRemove ->
            FavoritesManager.removeFavorite(itemToRemove)
            loadFavorites()
            updatePageUI()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
