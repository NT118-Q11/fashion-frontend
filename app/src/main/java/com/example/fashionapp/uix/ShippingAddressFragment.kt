package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ShippingAddressBinding
import com.example.fashionapp.data.UserManager

class ShippingAddressFragment : Fragment() {
    private var _binding: ShippingAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để inflate layout
        _binding = ShippingAddressBinding.inflate(inflater, container, false)
        // Trả về view gốc của layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Load and display user shipping information
        loadShippingAddress()

        binding.backButton.setOnClickListener {
            // Lệnh này sẽ quay lại màn hình trước đó trong Back Stack (tức là MyAccountFragment)
            findNavController().popBackStack()
        }

        // Edit button to navigate to profile or edit address
        binding.imageButton1.setOnClickListener {
            // Navigate to profile or open edit dialog
            // For now, just navigate back or to profile
            findNavController().popBackStack()
        }
    }

    /**
     * Load user shipping address information and populate the fields
     */
    private fun loadShippingAddress() {
        val user = userManager.getUser()
        val firstName = userManager.getFirstName()
        val lastName = userManager.getLastName()

        // Build full name
        val fullName = buildString {
            if (!firstName.isNullOrEmpty()) {
                append(firstName)
            }
            if (!lastName.isNullOrEmpty()) {
                if (isNotEmpty()) append(" ")
                append(lastName)
            }
        }.ifEmpty { user?.username ?: "Guest User" }

        // Set the values to the views
        binding.name.text = fullName
        binding.address.text = user?.userAddress ?: "No address provided"
        binding.phoneNumber.text = user?.phoneNumber ?: "No phone number"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }
}