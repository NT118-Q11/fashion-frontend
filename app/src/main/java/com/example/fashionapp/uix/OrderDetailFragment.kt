package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.AppRoute
import com.example.fashionapp.databinding.FragmentOrderDetailBinding
import com.example.fashionapp.adapter.OrderItemAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OrderDetailFragment : Fragment() {

    private var _binding: FragmentOrderDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup back button
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Setup bottom navigation
        setupBottomNavigation()

        val orderId = arguments?.getString("orderId")
        if (orderId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Invalid order ID", Toast.LENGTH_SHORT).show()
            return
        }

        setupRecyclerView()
        loadOrderDetails(orderId)
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            findNavController().navigate(com.example.fashionapp.R.id.homeFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(com.example.fashionapp.R.id.NotificationFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(com.example.fashionapp.R.id.cartFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(com.example.fashionapp.R.id.myAccountFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderItemAdapter(emptyList())
        binding.rvOrderItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrderItems.adapter = adapter
    }

    private fun loadOrderDetails(orderId: String) {
        lifecycleScope.launch {
            try {
                binding.progressBar.visibility = View.VISIBLE

                // Load order details on IO thread
                val order = withContext(Dispatchers.IO) {
                    AppRoute.order.getOrderById(orderId)
                }

                // Try to use items from order response first
                // If empty, call OrderItemApi separately
                val orderItems = if (order.items.isNotEmpty()) {
                    order.items
                } else {
                    // Fallback: Load order items using OrderItemApi on IO thread
                    withContext(Dispatchers.IO) {
                        AppRoute.orderItem.getOrderItemsByOrderId(orderId)
                    }
                }

                // Update UI
                binding.tvOrderId.text = "#${order.id?.take(8) ?: "N/A"}"
                binding.tvOrderStatus.text = order.status
                binding.tvOrderDate.text = formatDate(order.createdAt)
                binding.tvShippingAddress.text = order.shippingAddress
                binding.tvPaymentMethod.text = order.paymentMethod
                binding.tvTotalAmount.text = "$${String.format("%.2f", order.getTotal())}"

                adapter.updateItems(orderItems)

                binding.progressBar.visibility = View.GONE

                if (orderItems.isEmpty()) {
                    Toast.makeText(requireContext(), "No items found in this order", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load order details: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

