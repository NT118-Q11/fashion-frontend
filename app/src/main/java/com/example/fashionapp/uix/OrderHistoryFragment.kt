package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.databinding.FragmentOrderHistoryBinding
import com.example.fashionapp.model.OrderItem
import com.example.fashionapp.adapter.OrderAdapter

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabs()
        val sampleOrders = listOf(
            OrderItem("#ORD001", "20 Aug 2025", "Delivered", "$120"),
            OrderItem("#ORD002", "18 Aug 2025", "Cancelled", "$80"),
            OrderItem("#ORD003", "15 Aug 2025", "Refunded", "$60")
        )

        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = OrderAdapter(sampleOrders)
    }

    private fun setupTabs() {
        binding.tabLayoutStatus.addTab(
            binding.tabLayoutStatus.newTab().setText("Delivered")
        )
        binding.tabLayoutStatus.addTab(
            binding.tabLayoutStatus.newTab().setText("Cancelled")
        )
        binding.tabLayoutStatus.addTab(
            binding.tabLayoutStatus.newTab().setText("Refunded")
        )

        binding.tabLayoutStatus.addOnTabSelectedListener(object :
            com.google.android.material.tabs.TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab) {
                when (tab.position) {
                    0 -> loadOrders("DELIVERED")
                    1 -> loadOrders("CANCELLED")
                    2 -> loadOrders("REFUNDED")
                }
            }

            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab) {}
        })
    }

    private fun loadOrders(status: String) {
        // TODO: Sau này thay bằng API
        // Hiện tại chỉ demo UI
        println("Load orders with status: $status")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
