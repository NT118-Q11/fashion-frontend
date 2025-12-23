package com.example.fashionapp.uix

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
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityConfirmBinding
import com.example.fashionapp.model.OrderItemRequest
import com.example.fashionapp.model.OrderRequest
import kotlinx.coroutines.launch

class ConfirmFragment : Fragment() {

    private var _binding: ActivityConfirmBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userManager: UserManager
    private var shippingAddress: String = ""
    private var shippingFee: Int = 0

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

        loadOrderSummary()

        // Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
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

                // Create order items from cart
                val orderItems = cart.items.map { cartItem ->
                    OrderItemRequest(
                        productId = cartItem.productId,
                        quantity = cartItem.quantity,
                        price = cartItem.product?.price ?: 0.0
                    )
                }

                // Calculate total amount (cart total + shipping fee)
                val totalAmount = cart.totalPrice + shippingFee

                // Create order request
                val orderRequest = OrderRequest(
                    userId = userId,
                    items = orderItems,
                    totalAmount = totalAmount,
                    shippingAddress = shippingAddress,
                    paymentMethod = "COD",
                    status = "PENDING"
                )

                // Send order to backend
                val orderResponse = AppRoute.order.createOrder(orderRequest)

                // Clear cart after successful order
                CartManager.clearCart(userId)

                // Navigate to payment success with order ID
                val bundle = bundleOf("orderId" to orderResponse.id)
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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rcvOrder.adapter = null
        _binding = null
    }
}
