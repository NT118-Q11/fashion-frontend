package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.databinding.DetailsBinding
import com.google.android.material.tabs.TabLayoutMediator
import androidx.navigation.fragment.findNavController

class DetailsFragment: Fragment() {
    private var _binding: DetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageList = listOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        val adapter = ImageSliderAdapter(imageList)
        binding.viewPagerMain.adapter = adapter

        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { tab, position ->
            // No-op
        }.attach()

        // Back arrow
        binding.imageButton1.setOnClickListener { findNavController().navigateUp() }

        // Bottom buttons -> navigate to details_1 / details_2 / details_3
        binding.root.findViewById<View>(R.id.btn_info)?.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.root.findViewById<View>(R.id.btn_description)?.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details2Fragment)
        }
        binding.root.findViewById<View>(R.id.btn_rating)?.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}