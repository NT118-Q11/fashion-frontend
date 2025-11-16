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
            // tùy chỉnh tab nếu cần
        }.attach()

        // Lắng nghe dữ liệu từ ViewModel nếu cần:
        // viewModel.someLiveData.observe(viewLifecycleOwner) { ... }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}