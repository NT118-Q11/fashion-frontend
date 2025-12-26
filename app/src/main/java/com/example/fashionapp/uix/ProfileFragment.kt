package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ProfileBinding
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.data.UpdateNameRequest
import com.example.fashionapp.data.UpdateEmailRequest
import com.example.fashionapp.data.UpdatePhoneRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private var _binding: ProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để inflate layout
        _binding = ProfileBinding.inflate(inflater, container, false)
        // Trả về view gốc của layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Load and display user information
        loadUserProfile()

        binding.backButton.setOnClickListener {
            // Lệnh này sẽ quay lại màn hình trước đó trong Back Stack (tức là MyAccountFragment)
            findNavController().popBackStack()
        }

        // Save button to update profile
        binding.confirmButton.setOnClickListener {
            saveUserProfile()
        }

        // Bottom navigation bar
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myAccountFragment)
        }
    }

    /**
     * Load user profile information and populate the fields
     */
    private fun loadUserProfile() {
        val user = userManager.getUser()
        val firstName = userManager.getFirstName()
        val lastName = userManager.getLastName()

        // Set the values to the input fields
        binding.currentName.setText(firstName ?: "")
        binding.lastName2.setText(lastName ?: "")
        binding.emailAddress.setText(user?.email ?: "")
        binding.phoneNumber.setText(user?.phoneNumber ?: "")
    }

    /**
     * Save updated user profile information
     */
    private fun saveUserProfile() {
        val firstName = binding.currentName.text.toString().trim()
        val lastName = binding.lastName2.text.toString().trim()
        val email = binding.emailAddress.text.toString().trim()
        val phoneNumber = binding.phoneNumber.text.toString().trim()

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your full name", Toast.LENGTH_SHORT).show()
            return
        }

        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show()
            return
        }

        if (phoneNumber.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter phone number", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = userManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        binding.confirmButton.isEnabled = false
        binding.confirmButton.text = getString(R.string.updating_text)

        lifecycleScope.launch {
            try {
                // Update name, email, phone via API on IO thread
                withContext(Dispatchers.IO) {
                    // Update name
                    AppRoute.user.updateUserName(
                        userId,
                        UpdateNameRequest(firstName, lastName)
                    )

                    // Update email
                    AppRoute.user.updateUserEmail(
                        userId,
                        UpdateEmailRequest(email)
                    )

                    // Update phone
                    AppRoute.user.updateUserPhone(
                        userId,
                        UpdatePhoneRequest(phoneNumber)
                    )
                }

                // Update local storage with new values
                userManager.updateProfile(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    phoneNumber = phoneNumber
                )

                Toast.makeText(requireContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show()

                // Navigate back
                findNavController().popBackStack()

            } catch (e: Exception) {
                android.util.Log.e("ProfileFragment", "Error updating profile", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to update profile: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                // Reset button state
                binding.confirmButton.isEnabled = true
                binding.confirmButton.text = getString(R.string.confirm_button_text)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }

}