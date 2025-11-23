package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivitySearchBinding

class ActivitySearchFragment : Fragment() {
    private var _binding: ActivitySearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_homeFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_myAccountFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_notificationFragment)
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnFilter.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_activitySearchViewFragment)
        }

        binding.centerSearchIcon.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_activitySearchViewFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}