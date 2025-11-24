package com.example.fashionapp.uix

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ReelPagerAdapter
import com.example.fashionapp.databinding.ActivityHomeBinding
import com.example.fashionapp.model.ReelItem

class HomeFragment : Fragment() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    private var reelAdapter: ReelPagerAdapter? = null
    private var reelIndicatorView: VerticalReelIndicator? = null

    private val allItems: MutableList<ReelItem> = mutableListOf()
    private var selectedCategory: String = "WOMEN"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Remove/hide the right slider indicator
        binding.reelIndicatorContainer.visibility = View.GONE
        reelIndicatorView = null

        // Setup vertical ViewPager2 to behave like a reel
        reelAdapter = ReelPagerAdapter(requireContext())
        binding.reelPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = reelAdapter
            // Disable overscroll and scrollbar to remove any right-side scroll visuals
            (getChildAt(0) as? RecyclerView)?.apply {
                overScrollMode = View.OVER_SCROLL_NEVER
                isVerticalScrollBarEnabled = false
            }
        }

        // Per-label top-area brightness to style category bar text (WOMEN/MAN/KIDS)
        reelAdapter?.onTopTextColorsSuggested = { leftColor, centerColor, rightColor ->
            updateCategoryBarStyles(leftColor, centerColor, rightColor)
        }

        // Keep category labels in sync when page changes (use cached colors)
        binding.reelPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                reelAdapter?.getTopColorsFor(position)?.let { (l, c, r) ->
                    updateCategoryBarStyles(l, c, r)
                }
            }
        })

        // Build master data once
        allItems.clear()
        val specs = listOf(
            Triple("WOMEN", "woman", "women"),
            Triple("MAN", "men", "men"),
            Triple("KIDS", "kid", "kids")
        )
        for (spec in specs) {
            val (brand, folder, prefix) = spec
            for (n in 1..3) {
                val filePath = "$folder/${prefix}${n}.jpg"
                Log.d("HomeFragment", "Adding reel asset: $filePath")
                val name = when (brand) {
                    "WOMEN" -> "Cardigan Pink $n"
                    "MAN" -> "Denim Jacket $n"
                    else -> "Kids Outfit $n"
                }
                allItems.add(ReelItem(filePath, brand, name, "$${(80..140).random()}"))
            }
        }

        // Category clicks -> filter items
        binding.catWomen.setOnClickListener { setCategory("WOMEN") }
        binding.catMan.setOnClickListener { setCategory("MAN") }
        binding.catKids.setOnClickListener { setCategory("KIDS") }

        // Default selection
        setCategory(selectedCategory)

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myAccountFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.NotificationFragment)
        }

        binding.icFavTop.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_MyFavoritesFragment)
        }

        binding.icSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_activitySearchFragment)
        }
    }

    private fun setCategory(category: String) {
        selectedCategory = category
        // Filter
        val filtered = allItems.filter { it.brand == category }
        reelAdapter?.submit(filtered)
        binding.reelPager.setCurrentItem(0, false)
        // Style active tab
        styleCategoryTabs(category)
        // If we already have cached top colors for first page, apply now
        reelAdapter?.getTopColorsFor(0)?.let { (l, c, r) ->
            updateCategoryBarStyles(l, c, r)
        }
    }

    private fun styleCategoryTabs(active: String) {
        fun TextView.setActive(a: Boolean) {
            alpha = if (a) 1f else 0.75f
            setTypeface(null, if (a) Typeface.BOLD else Typeface.NORMAL)
        }
        binding.catWomen.setActive(active == "WOMEN")
        binding.catMan.setActive(active == "MAN")
        binding.catKids.setActive(active == "KIDS")
    }

    private fun TextView.applyReadableStyle(c: Int) {
        setTextColor(c)
        if (c == Color.WHITE) {
            setShadowLayer(8f, 0f, 1f, 0xCC000000.toInt())
        } else {
            setShadowLayer(4f, 0f, 1f, 0x22000000)
        }
    }

    private fun updateCategoryBarStyles(left: Int, center: Int, right: Int) {
        binding.catWomen.applyReadableStyle(left)
        binding.catMan.applyReadableStyle(center)
        binding.catKids.applyReadableStyle(right)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}