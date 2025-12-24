package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.data.UpdateAddressRequest
import com.example.fashionapp.data.UpdateNameRequest
import com.example.fashionapp.data.UpdatePhoneRequest
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityCheckoutBinding
import com.example.fashionapp.databinding.DialogEditAddressBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch

class CheckoutFragment : Fragment() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!

    private var basePrice: Double = 0.0
    private var currentShippingFee = 0
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Get cart total from arguments
        basePrice = arguments?.getDouble("cartTotal", 0.0) ?: 0.0

        // Load shipping address from user profile
        loadShippingAddress()

        setupShippingSpinner()
        setupActions()
        updateTotal(0)
    }

    //SHIPPING
    private fun setupShippingSpinner() {

        val shippingList = listOf(
            "Standard (FREE)",
            "Next day (+$10)"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_spinner_selected,
            shippingList
        )
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown)
        binding.spinnerShipping.adapter = adapter

        binding.spinnerShipping.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    currentShippingFee = if (position == 0) 0 else 10
                    updateTotal(currentShippingFee)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    //ACTION
    private fun setupActions() {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.cardAddress.setOnClickListener {
            showEditAddressDialog()
        }

        binding.btnPlaceOrder.setOnClickListener {
            // Get shipping address from the card
            val shippingAddress = "${binding.tvAddressName.text}\n" +
                    "${binding.tvAddressStreet.text}\n" +
                    "${binding.tvAddressCity.text}\n" +
                    "${binding.tvAddressPhone.text}"

            // Pass shipping info and pricing to ConfirmFragment
            val bundle = bundleOf(
                "shippingAddress" to shippingAddress,
                "shippingFee" to currentShippingFee,
                "subtotal" to basePrice
            )

            findNavController().navigate(
                R.id.action_checkoutFragment_to_confirmFragment,
                bundle
            )
        }
    }

    //TOTAL
    private fun updateTotal(shippingFee: Int) {
        val total = basePrice + shippingFee
        binding.tvSubtotalPrice.text = "$${String.format("%.2f", total)}"
    }

    //LOAD ADDRESS
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

        // Parse address (format: street | city)
        val address = user?.userAddress ?: ""

        val street: String
        val city: String

        if (address.isNotEmpty() && address.contains("|")) {
            val addressParts = address.split("|").map { it.trim() }
            street = addressParts.getOrNull(0)?.takeIf { it.isNotEmpty() } ?: "No address provided"
            city = addressParts.getOrNull(1)?.takeIf { it.isNotEmpty() } ?: ""
        } else {
            // Handle legacy format or single-line address
            street = address.takeIf { it.isNotEmpty() } ?: "No address provided"
            city = ""
        }

        val phone = user?.phoneNumber?.takeIf { it.isNotEmpty() } ?: "No phone number"

        // Set the values to the views - ensure never empty
        binding.tvAddressName.text = fullName
        binding.tvAddressStreet.text = street
        binding.tvAddressCity.text = city
        binding.tvAddressPhone.text = phone
    }

    //EDIT ADDRESS DIALOG
    private fun showEditAddressDialog() {
        val dialogBinding = DialogEditAddressBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // Pre-fill with current data
        dialogBinding.etName.setText(binding.tvAddressName.text)
        dialogBinding.etStreet.setText(binding.tvAddressStreet.text)
        dialogBinding.etCity.setText(binding.tvAddressCity.text)
        dialogBinding.etPhone.setText(binding.tvAddressPhone.text)

        // Cancel button
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        // Save button
        dialogBinding.btnSave.setOnClickListener {
            saveShippingAddress(dialogBinding, dialog)
        }

        dialog.show()
    }

    //SAVE ADDRESS
    private fun saveShippingAddress(dialogBinding: DialogEditAddressBinding, dialog: BottomSheetDialog) {
        val newName = dialogBinding.etName.text.toString().trim()
        val newStreet = dialogBinding.etStreet.text.toString().trim()
        val newCity = dialogBinding.etCity.text.toString().trim()
        val newPhone = dialogBinding.etPhone.text.toString().trim()

        // Validate inputs
        var hasError = false

        if (newName.isEmpty()) {
            dialogBinding.nameInputLayout.error = "Name is required"
            hasError = true
        } else {
            dialogBinding.nameInputLayout.error = null
        }

        if (newStreet.isEmpty()) {
            dialogBinding.streetInputLayout.error = "Street address is required"
            hasError = true
        } else {
            dialogBinding.streetInputLayout.error = null
        }

        if (newCity.isEmpty()) {
            dialogBinding.cityInputLayout.error = "City is required"
            hasError = true
        } else {
            dialogBinding.cityInputLayout.error = null
        }

        if (newPhone.isEmpty()) {
            dialogBinding.phoneInputLayout.error = "Phone number is required"
            hasError = true
        } else {
            dialogBinding.phoneInputLayout.error = null
        }

        if (hasError) return

        val userId = userManager.getUserId()
        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading state
        dialogBinding.btnSave.isEnabled = false
        dialogBinding.btnSave.text = "Saving..."

        // Parse name into first and last name
        val nameParts = newName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""

        // Combine address in format: street | city
        val combinedAddress = "$newStreet | $newCity"

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
                    UpdateAddressRequest(combinedAddress)
                )

                // Update local storage
                userManager.updateProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = newPhone,
                    address = combinedAddress
                )

                // Update display
                binding.tvAddressName.text = newName
                binding.tvAddressStreet.text = newStreet
                binding.tvAddressCity.text = newCity
                binding.tvAddressPhone.text = newPhone

                // Show success message
                Toast.makeText(
                    requireContext(),
                    "Shipping address updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Close dialog
                dialog.dismiss()

            } catch (e: Exception) {
                android.util.Log.e("CheckoutFragment", "Error updating address", e)
                Toast.makeText(
                    requireContext(),
                    "Failed to update address: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                // Reset button state
                dialogBinding.btnSave.isEnabled = true
                dialogBinding.btnSave.text = "Save"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
