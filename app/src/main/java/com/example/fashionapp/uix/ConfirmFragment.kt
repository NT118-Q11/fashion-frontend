package com.example.fashionapp.uix

import android.util.Log
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ConfirmAdapter
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UpdateAddressRequest
import com.example.fashionapp.data.UpdateNameRequest
import com.example.fashionapp.data.UpdatePhoneRequest
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityConfirmBinding
import com.example.fashionapp.databinding.DialogEditAddressBinding
import com.example.fashionapp.model.OrderItemRequest
import com.example.fashionapp.model.OrderRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConfirmFragment : Fragment() {

    private var _binding: ActivityConfirmBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userManager: UserManager
    private var shippingAddress: String = ""
    private var shippingFee: Int = 0

    // Store parsed address parts for display
    private var customerName: String = ""
    private var streetAddress: String = ""
    private var cityAddress: String = ""
    private var phoneNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityConfirmBinding.inflate(inflater, container, false)
        userManager = UserManager.getInstance(requireContext())

        // Get shipping info from arguments
        shippingAddress = arguments?.getString("shippingAddress") ?: "No address provided"
        shippingFee = arguments?.getInt("shippingFee") ?: 0

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set default values first to avoid empty TextView issues
        binding.tvCustomerName.text = "Loading..."
        binding.tvCustomerAddress.text = "Loading address..."

        // Load and display shipping address
        loadShippingAddress()

        loadOrderSummary()

        // Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Edit address - Click on address row
        binding.addressRow.setOnClickListener {
            showEditAddressDialog()
        }

        // Confirm - Create order and navigate to success
        binding.ConfirmButton.setOnClickListener {
            createOrder()
        }
    }

    private fun loadOrderSummary() {
        val userId = userManager.getUserId() ?: return
        
        lifecycleScope.launch {
            val cart = CartManager.getCart(userId)
            if (cart != null) {
                // Setup RecyclerView
                val adapter = ConfirmAdapter(cart.items)
                binding.rcvOrder.layoutManager = LinearLayoutManager(context)
                binding.rcvOrder.adapter = adapter

                // Tính tổng (cart total + shipping fee)
                val total = cart.totalPrice + shippingFee
                binding.tvTotalAmount.text = "$${String.format("%.2f", total)}"
            }
        }
    }

    private fun createOrder() {
        val userId = userManager.getUserId()
        if (userId == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        binding.ConfirmButton.isEnabled = false

        lifecycleScope.launch {
            try {
                val cart = CartManager.getCart(userId)
                if (cart == null || cart.items.isEmpty()) {
                    Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                    binding.ConfirmButton.isEnabled = true
                    return@launch
                }

                // Prepare order items data from cart with full product info
                data class CartItemData(
                    val productId: String,
                    val productName: String,
                    val quantity: Int,
                    val price: Double,
                    val size: String?,
                    val color: String?
                )

                val cartItemsData = cart.items.mapNotNull { cartItem ->
                    val product = cartItem.product
                    if (product != null) {
                        CartItemData(
                            productId = product.id,
                            productName = product.name,
                            quantity = cartItem.quantity,
                            price = product.price,
                            // Get first available size and color as default
                            size = product.sizes?.firstOrNull(),
                            color = product.colors?.firstOrNull()
                        )
                    } else {
                        null
                    }
                }

                // Calculate total amount (cart total + shipping fee)
                val totalPrice = cart.totalPrice + shippingFee

                // Use the current shipping address from display
                val currentShippingAddress = "$streetAddress | $cityAddress"

                // Create order request (without items, backend will handle separately)
                val orderRequest = OrderRequest(
                    userId = userId,
                    items = emptyList(), // Send empty, we'll create items separately
                    totalPrice = totalPrice,
                    shippingAddress = currentShippingAddress,
                    paymentMethod = "COD",
                    status = "SUCCESSFUL"
                )

                // Send order to backend on IO thread
                val orderResponse = withContext(Dispatchers.IO) {
                    AppRoute.order.createOrder(orderRequest)
                }
                val orderId = orderResponse.id

                if (orderId == null) {
                    Toast.makeText(context, "Failed to create order: No order ID returned", Toast.LENGTH_LONG).show()
                    binding.ConfirmButton.isEnabled = true
                    return@launch
                }

                // Now create order items with the orderId
                var allItemsCreated = true
                for (itemData in cartItemsData) {
                    try {
                        val orderItemRequest = OrderItemRequest(
                            orderId = orderId,
                            productId = itemData.productId,
                            productName = itemData.productName,
                            size = itemData.size,
                            color = itemData.color,
                            quantity = itemData.quantity,
                            priceAtPurchase = itemData.price
                        )
                        withContext(Dispatchers.IO) {
                            AppRoute.orderItem.createOrderItem(orderItemRequest)
                        }
                        Log.d("ConfirmFragment", "Created order item for product: ${itemData.productName}")
                    } catch (e: Exception) {
                        Log.e("ConfirmFragment", "Failed to create order item for product: ${itemData.productId}", e)
                        allItemsCreated = false
                    }
                }

                if (!allItemsCreated) {
                    Log.w("ConfirmFragment", "Some order items failed to create")
                }

                // Clear cart after successful order
                val cartCleared = CartManager.clearCart(userId)
                if (!cartCleared) {
                    Log.e("ConfirmFragment", "Failed to clear cart after order creation")
                    // Still proceed to success screen, but log the error
                }

                // Extract product IDs from cart items for rating
                val productIds = cartItemsData.map { it.productId }.toTypedArray()

                // Navigate to payment success with order ID and product IDs
                val bundle = bundleOf(
                    "orderId" to orderResponse.id,
                    "productIds" to productIds
                )
                findNavController().navigate(
                    R.id.action_confirmFragment_to_paymentSuccessFragment,
                    bundle
                )

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to create order: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
                binding.ConfirmButton.isEnabled = true
            }
        }
    }

    //LOAD SHIPPING ADDRESS
    private fun loadShippingAddress() {
        val user = userManager.getUser()
        val firstName = userManager.getFirstName()
        val lastName = userManager.getLastName()

        // Build full name
        customerName = buildString {
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

        if (address.isNotEmpty() && address.contains("|")) {
            val addressParts = address.split("|").map { it.trim() }
            streetAddress = addressParts.getOrNull(0)?.takeIf { it.isNotEmpty() } ?: "No address provided"
            cityAddress = addressParts.getOrNull(1)?.takeIf { it.isNotEmpty() } ?: ""
        } else {
            // Handle legacy format or single-line address
            streetAddress = address.takeIf { it.isNotEmpty() } ?: "No address provided"
            cityAddress = ""
        }

        phoneNumber = user?.phoneNumber?.takeIf { it.isNotEmpty() } ?: "No phone number"

        // Update display
        updateAddressDisplay()
    }

    //UPDATE ADDRESS DISPLAY
    private fun updateAddressDisplay() {
        // Ensure name is never empty
        binding.tvCustomerName.text = customerName.takeIf { it.isNotEmpty() } ?: "Guest User"

        // Format address display - ensure it's never empty
        val addressText = buildString {
            if (streetAddress.isNotEmpty() && streetAddress != "No address provided") {
                append(streetAddress)
            } else {
                append("No address provided")
            }

            if (cityAddress.isNotEmpty()) {
                append("\n")
                append(cityAddress)
            }

            if (phoneNumber.isNotEmpty() && phoneNumber != "No phone number") {
                append("\n")
                append(phoneNumber)
            }
        }

        binding.tvCustomerAddress.text = addressText.takeIf { it.isNotEmpty() } ?: "Please add your address"
    }

    //EDIT ADDRESS DIALOG
    private fun showEditAddressDialog() {
        val dialogBinding = DialogEditAddressBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogBinding.root)

        // Pre-fill with current data
        dialogBinding.etName.setText(customerName)
        dialogBinding.etStreet.setText(streetAddress)
        dialogBinding.etCity.setText(cityAddress)
        dialogBinding.etPhone.setText(phoneNumber)

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

    //SAVE SHIPPING ADDRESS
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
                // Update name, phone, address via API on IO thread
                withContext(Dispatchers.IO) {
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
                }

                // Update local storage
                userManager.updateProfile(
                    firstName = firstName,
                    lastName = lastName,
                    phoneNumber = newPhone,
                    address = combinedAddress
                )

                // Update local variables
                customerName = newName
                streetAddress = newStreet
                cityAddress = newCity
                phoneNumber = newPhone
                shippingAddress = combinedAddress

                // Update display
                updateAddressDisplay()

                // Show success message
                Toast.makeText(
                    requireContext(),
                    "Shipping address updated successfully",
                    Toast.LENGTH_SHORT
                ).show()

                // Close dialog
                dialog.dismiss()

            } catch (e: Exception) {
                android.util.Log.e("ConfirmFragment", "Error updating address", e)
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
        binding.rcvOrder.adapter = null
        _binding = null
    }
}
