package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.data.UserManager
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

        // Settings Button - Show Popup Menu with Logout
        binding.settingButton.setOnClickListener {
            showSettingsMenu(it)
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

    /**
     * Show settings dropdown menu with logout option
     */
    private fun showSettingsMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.account_settings_menu, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    /**
     * Show confirmation dialog before logout
     */
    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Log Out")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ ->
                handleLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    /**
     * Handle user logout
     */
    private fun handleLogout() {
        // Clear user data from SharedPreferences
        val userManager = UserManager.getInstance(requireContext())
        userManager.logout()

        // Navigate to sign in screen and clear back stack
        findNavController().navigate(R.id.action_myAccountFragment_to_signInFragment)

        // Optional: Show a toast message
        android.widget.Toast.makeText(
            requireContext(),
            "Successfully logged out",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}