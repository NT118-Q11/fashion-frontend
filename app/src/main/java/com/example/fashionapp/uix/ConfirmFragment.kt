package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ConfirmAdapter
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityConfirmBinding
import kotlinx.coroutines.launch

class ConfirmFragment : Fragment() {

    private var _binding: ActivityConfirmBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userManager: UserManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityConfirmBinding.inflate(inflater, container, false)
        userManager = UserManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadOrderSummary()

        // Back
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        // Confirm
        binding.ConfirmButton.setOnClickListener {
            findNavController()
                .navigate(R.id.action_confirmFragment_to_paymentSuccessFragment)
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

                // Tính tổng
                binding.tvTotalAmount.text = "$${String.format("%.2f", cart.totalPrice)}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rcvOrder.adapter = null
        _binding = null
    }
}
