package com.example.fashionapp.uix

import android.util.Log
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Add vertical indicator programmatically
        reelIndicatorView = VerticalReelIndicator(requireContext())
        binding.reelIndicatorContainer.addView(reelIndicatorView)

        // Setup vertical ViewPager2 to behave like a reel
        reelAdapter = ReelPagerAdapter(requireContext())
        binding.reelPager.apply {
            orientation = ViewPager2.ORIENTATION_VERTICAL
            adapter = reelAdapter
        }

        // Prepare sample data from assets (women, man, kids)
        val items = mutableListOf<ReelItem>()
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
                items.add(ReelItem(filePath, brand, name, "$${(80..140).random()}"))
            }
        }

        reelAdapter?.submit(items)
        reelIndicatorView?.setTotal(items.size)

        binding.reelPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                reelIndicatorView?.onPageScrolled(position, positionOffset)
            }
            override fun onPageSelected(position: Int) {
                reelIndicatorView?.onPageSelected(position)
            }
        })

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}