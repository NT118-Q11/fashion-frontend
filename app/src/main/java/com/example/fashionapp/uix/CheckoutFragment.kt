package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ActivityCheckoutBinding

class CheckoutFragment : Fragment() {

    private var _binding: ActivityCheckoutBinding? = null
    private val binding get() = _binding!!

    private val basePrice = 240

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
                    val shippingFee = if (position == 0) 0 else 10
                    updateTotal(shippingFee)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
    }

    //ACTION
    private fun setupActions() {

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnPlaceOrder.setOnClickListener {
            // Payment mặc định là COD
            findNavController().navigate(
                R.id.action_checkoutFragment_to_confirmFragment
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
