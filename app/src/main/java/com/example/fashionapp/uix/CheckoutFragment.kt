package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivityCheckoutBinding

class CheckoutFragment : Fragment() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPayment.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_paymentFragment)
        }

        binding.btnPay.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_confirmFragment)
        }

        binding.btnDelivery.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_deliveryFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}