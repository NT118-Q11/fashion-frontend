package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ChangePasswordBinding
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.data.UpdatePasswordRequest
import kotlinx.coroutines.launch

class ChangePasswordFragment: Fragment() {

    private var _binding: ChangePasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để inflate layout
        _binding = ChangePasswordBinding.inflate(inflater, container, false)
        // Trả về view gốc của layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        binding.backButton.setOnClickListener {
            // Lệnh này sẽ quay lại màn hình trước đó trong Back Stack (tức là MyAccountFragment)
            findNavController().popBackStack()
        }

        // Save button to change password
        binding.confirmButton.setOnClickListener {
            changePassword()
        }

        // Bottom navigation bar
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_homeFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_cartFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_notificationFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_myAccountFragment)
        }
    }

    /**
     * Change user password
     */
    private fun changePassword() {
        val oldPassword = binding.currentPassword.text.toString().trim()
        val newPassword = binding.newPassword.text.toString().trim()
        val confirmPassword = binding.confirmPassword.text.toString().trim()

        // Validate inputs
        if (oldPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your old password", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter new password", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword.length < 6) {
            Toast.makeText(requireContext(), "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return
        }

        if (newPassword != confirmPassword) {
            Toast.makeText(requireContext(), "New passwords do not match", Toast.LENGTH_SHORT).show()
            return
        }

        if (oldPassword == newPassword) {
            Toast.makeText(requireContext(), "New password must be different from old password", Toast.LENGTH_SHORT).show()
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
                // Call API to update password
                val response = AppRoute.user.updateUserPassword(
                    userId,
                    UpdatePasswordRequest(oldPassword, newPassword)
                )

                Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()

                // Clear input fields
                binding.currentPassword.text?.clear()
                binding.newPassword.text?.clear()
                binding.confirmPassword.text?.clear()

                // Navigate back
                findNavController().popBackStack()

            } catch (e: Exception) {
                android.util.Log.e("ChangePasswordFragment", "Error changing password", e)

                val errorMessage = when {
                    e.message?.contains("400") == true || e.message?.contains("Bad Request") == true ->
                        "Invalid old password or new password does not meet requirements"
                    e.message?.contains("404") == true ->
                        "User not found"
                    else ->
                        "Failed to change password: ${e.message}"
                }

                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
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