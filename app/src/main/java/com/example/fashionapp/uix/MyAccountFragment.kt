package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.databinding.MyAccountBinding

class MyAccountFragment: Fragment() {

    private var _binding: MyAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MyAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Click Setting
        binding.settingButton.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_settingFragment)
        }

        // 2. Click Password
        binding.passwordButton.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_changePasswordFragment)
        }

        // 3. Click Shipping Address
        binding.addressButton.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_shippingAddressFragment)
        }

        binding.profileButton.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_profileFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_cartFragment)
        }

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_homeFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_myAccountFragment_to_notificationFragment)
        }

        // 4. Click Orders
        binding.tvOrderhistory.setOnClickListener {
            findNavController().navigate(
                R.id.action_myAccountFragment_to_ordersFragment
            )
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}