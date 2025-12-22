package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ActivityCheckoutBinding

class CheckoutFragment : Fragment() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!

    private val basePrice = 240
    private var currentShippingFee = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            // TODO: Navigate to address edit screen or show address picker dialog
            // For now, you can implement a dialog or navigate to an address screen
            // Example: findNavController().navigate(R.id.action_checkoutFragment_to_addressFragment)
        }

        binding.btnPlaceOrder.setOnClickListener {
            // Get shipping address from the card
            val shippingAddress = "${binding.tvAddressName.text}\n" +
                    "${binding.tvAddressStreet.text}\n" +
                    "${binding.tvAddressCity.text}\n" +
                    "${binding.tvAddressPhone.text}"

            // Pass shipping info to ConfirmFragment
            val bundle = bundleOf(
                "shippingAddress" to shippingAddress,
                "shippingFee" to currentShippingFee
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
        binding.tvSubtotalPrice.text = "$$total"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
