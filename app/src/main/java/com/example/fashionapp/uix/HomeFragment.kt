package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivityHomeBinding



class HomeFragment : Fragment() {
    private var _binding: ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }
}