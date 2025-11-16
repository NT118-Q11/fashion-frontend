package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.databinding.ActivityMyFavoritesBinding
import com.example.fashionapp.model.FavoriteItem
import com.example.fashionapp.adapter.FavoritesAdapter
import com.example.fashionapp.R
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

class MyFavoritesFragment : Fragment() {
    private var _binding: ActivityMyFavoritesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityMyFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val items = listOf(
            FavoriteItem("LAMEREI", "reversible angora cardigan", "$120", R.drawable.sample_woman),
            FavoriteItem("FENDI", "cotton jacket", "$210", R.drawable.sample_woman),
            FavoriteItem("CHANEL", "classic coat", "$350", R.drawable.sample_woman),
        )

        binding.rvFavorites.adapter = FavoritesAdapter(items)

        // Quay lại
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

    override fun onDestroyView() {
        super.onDestroyView()
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }

}
