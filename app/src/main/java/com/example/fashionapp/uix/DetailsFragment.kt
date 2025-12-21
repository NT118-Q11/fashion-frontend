package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.data.CartItem
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.databinding.ProductDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailsFragment : Fragment() {

    private var _binding: ProductDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Danh sách ảnh sản phẩm
        val imageList = listOf(
            R.drawable.model_image_1,
            R.drawable.model_image_2,
            R.drawable.model_image_3
        )

        // Adapter cho ViewPager2
        binding.viewPagerProduct.adapter = ImageSliderAdapter(imageList)

        // Liên kết TabLayout với ViewPager2
        TabLayoutMediator(binding.tabLayoutProduct, binding.viewPagerProduct) { _, _ -> }.attach()

        // Nút back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // 3 nút chuyển fragment
        binding.tabInfo.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.tabDescription.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details2Fragment)
        }
        binding.tabRating.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }

        // Nút ADD TO CART
        binding.btnAddToCart.setOnClickListener {
            val item = CartItem(
                id = 1,
                title = "LAMEREI",
                description = "Recycle Boucle Knit Cardigan Pink",
                price = 120.0,
                imageRes = R.drawable.model_image_1,
                quantity = 1
            )

            CartManager.addItem(item)
            Toast.makeText(requireContext(), "Added to cart!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
