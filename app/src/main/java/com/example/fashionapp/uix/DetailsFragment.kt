package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ImageSliderAdapter
import com.example.fashionapp.data.CartItem
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.FavoritesManager
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.databinding.DetailsBinding
import com.google.android.material.tabs.TabLayoutMediator

class DetailsFragment : Fragment() {

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

        binding.viewPagerMain.adapter = ImageSliderAdapter(imageList)

        TabLayoutMediator(binding.tabLayoutMain, binding.viewPagerMain) { _, _ -> }.attach()

        binding.imageButton1.setOnClickListener {
            findNavController().popBackStack()
        }

        // Favorite Logic
        val favItem = FavoriteItem(
            id = "details_1", // Using a fixed ID for this sample detail page
            name = "LAMEREI",
            desc = "Recycle Boucle Knit Cardigan Pink",
            price = "$120",
            imageRes = R.drawable.model_image_1
        )

        fun updateFavoriteIcon() {
            val isFav = FavoritesManager.isFavorite(favItem)
            binding.btnFavoriteDetails.setImageResource(
                if (isFav) R.drawable.ic_favorite_filled else R.drawable.ic_favorite_border
            )
            binding.btnFavoriteDetails.setColorFilter(
                if (isFav) Color.parseColor("#E07A5F") else Color.BLACK
            )
        }

        updateFavoriteIcon()

        binding.btnFavoriteDetails.setOnClickListener {
            if (FavoritesManager.isFavorite(favItem)) {
                FavoritesManager.removeFavorite(favItem)
            } else {
                FavoritesManager.addFavorite(favItem)
            }
            updateFavoriteIcon()
        }

        // 3 nút chuyển fragment
        binding.btnInfo.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details1Fragment)
        }
        binding.btnDescription.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details2Fragment)
        }
        binding.btnRating.setOnClickListener {
            findNavController().navigate(R.id.action_detailsFragment_to_details3Fragment)
        }

        // NÚT ADD TO CART
        binding.addToCartButton.setOnClickListener {

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
