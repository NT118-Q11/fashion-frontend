package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ActivityPaymentSuccessBinding


class PaymentSuccessFragment : Fragment() {
    private var _binding: ActivityPaymentSuccessBinding? = null
    private val binding get() = _binding!!

    private var orderId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityPaymentSuccessBinding.inflate(inflater, container, false)

        // Get orderId from arguments
        orderId = arguments?.getString("orderId")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Optional: Display order ID if you have a TextView for it
        // binding.tvOrderId?.text = "Order ID: $orderId"

        binding.btnBackHome.setOnClickListener {
            // Navigate to home and clear back stack so user can't go back to payment screens
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}