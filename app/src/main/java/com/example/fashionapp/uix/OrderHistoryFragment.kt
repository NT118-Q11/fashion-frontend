package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.databinding.FragmentOrderHistoryBinding
import com.example.fashionapp.adapter.OrderHistoryAdapter
import com.example.fashionapp.data.UserManager
import kotlinx.coroutines.launch

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup back button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
        setupBottomNavigation()
        loadOrders() // Load all successful orders
    }

    private fun setupBottomNavigation() {
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.homeFragment)
        }

        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.NotificationFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.cartFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.myAccountFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter(emptyList())
        adapter.setOnItemClickListener { order ->
            // Navigate to order detail with orderId
            if (order.id != null) {
                val bundle = bundleOf("orderId" to order.id)
                findNavController().navigate(
                    R.id.action_orderHistoryFragment_to_orderDetailFragment,
                    bundle
                )
            } else {
                Toast.makeText(requireContext(), "Order ID not found", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun loadOrders() {
        val userManager = UserManager.getInstance(requireContext())
        val userId = userManager.getUser()?.id ?: run {
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                binding.rvOrders.visibility = View.GONE

                // Get all orders by user (showing all successful/completed orders)
                val allOrders = AppRoute.order.getOrdersByUserId(userId)

                // You can filter for specific statuses if needed, e.g., DELIVERED, SUCCESS
                // For now, showing all orders
                val successOrders = allOrders.filter {
                    it.status.equals("DELIVERED", ignoreCase = true) ||
                    it.status.equals("SUCCESS", ignoreCase = true) ||
                    it.status.equals("COMPLETED", ignoreCase = true)
                }

                adapter.updateOrders(successOrders)
                binding.rvOrders.visibility = View.VISIBLE

                if (successOrders.isEmpty()) {
                    Toast.makeText(requireContext(), "No successful orders found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                binding.rvOrders.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Failed to load orders: ${e.message}", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
