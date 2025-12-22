package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ShippingAddressBinding
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.data.UpdateAddressRequest
import com.example.fashionapp.data.UpdateNameRequest
import com.example.fashionapp.data.UpdatePhoneRequest
import kotlinx.coroutines.launch

class ShippingAddressFragment : Fragment() {
    private var _binding: ShippingAddressBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager
    private var isEditMode = false

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

        // Edit button to toggle edit mode
        binding.imageButton1.setOnClickListener {
            toggleEditMode()
        }

        // Save button
        binding.btnSave.setOnClickListener {
            saveShippingAddress()
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            toggleEditMode()
        }

        // Bottom navigation bar
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_shippingAddressFragment_to_homeFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_shippingAddressFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_shippingAddressFragment_to_notificationFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_shippingAddressFragment_to_myAccountFragment)
        }
    }

    /**
     * Toggle between display mode and edit mode
     */
    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            // Switch to edit mode
            binding.displayMode.visibility = View.GONE
            binding.editMode.visibility = View.VISIBLE
            binding.imageButton1.visibility = View.GONE

            // Populate input fields with current values
            binding.nameInput.setText(binding.name.text)
            binding.addressInput.setText(binding.address.text)
            binding.phoneInput.setText(binding.phoneNumber.text)
        } else {
            // Switch to display mode
            binding.displayMode.visibility = View.VISIBLE
            binding.editMode.visibility = View.GONE
            binding.imageButton1.visibility = View.VISIBLE
        }
    }

    /**
     * Save the edited shipping address information
     */
    private fun saveShippingAddress() {
        val newName = binding.nameInput.text.toString().trim()
        val newAddress = binding.addressInput.text.toString().trim()
        val newPhone = binding.phoneInput.text.toString().trim()

        // Validate inputs
        if (newName.isEmpty()) {
            binding.nameInputLayout.error = "Name is required"
            return
        } else {
            binding.nameInputLayout.error = null
        }

        if (newAddress.isEmpty()) {
            binding.addressInputLayout.error = "Address is required"
            return
        } else {
            binding.addressInputLayout.error = null
        }

        if (newPhone.isEmpty()) {
            binding.phoneInputLayout.error = "Phone number is required"
            return
        } else {
            binding.phoneInputLayout.error = null
        }

        val userId = userManager.getUserId()
        if (userId.isNullOrEmpty()) {
            android.widget.Toast.makeText(
                requireContext(),
                "User not logged in",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Show loading state
        binding.btnSave.isEnabled = false
        binding.btnSave.text = getString(R.string.saving_text)

        // Parse name into first and last name
        val nameParts = newName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        lifecycleScope.launch {
            try {
                // Update name via API
                AppRoute.user.updateUserName(
                    userId,
                    UpdateNameRequest(firstName, lastName)
                )

                // Update phone via API
                AppRoute.user.updateUserPhone(
                    userId,
                    UpdatePhoneRequest(newPhone)
                )

                // Update address via API
                AppRoute.user.updateUserAddress(
                    userId,
                    UpdateAddressRequest(newAddress)
                )

                // Update local storage
                userManager.updateProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = newPhone,
                    address = newAddress
                )

                // Update display
                binding.name.text = newName
                binding.address.text = newAddress
                binding.phoneNumber.text = newPhone

                // Switch back to display mode
                toggleEditMode()

                // Show success message
                android.widget.Toast.makeText(
                    requireContext(),
                    "Shipping address updated successfully",
                    android.widget.Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                android.util.Log.e("ShippingAddressFragment", "Error updating address", e)
                android.widget.Toast.makeText(
                    requireContext(),
                    "Failed to update address: ${e.message}",
                    android.widget.Toast.LENGTH_LONG
                ).show()
            } finally {
                // Reset button state
                binding.btnSave.isEnabled = true
                binding.btnSave.text = getString(R.string.save_text)
            }
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