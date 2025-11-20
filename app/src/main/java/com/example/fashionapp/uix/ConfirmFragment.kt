package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ConfirmAdapter
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.databinding.ActivityConfirmBinding

class ConfirmFragment : Fragment() {

    private var _binding: ActivityConfirmBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy danh sách giỏ hàng thật
        val items = CartManager.getItems()

        // Setup RecyclerView
        val adapter = ConfirmAdapter(items)
        binding.rcvOrder.layoutManager = LinearLayoutManager(context)
        binding.rcvOrder.adapter = adapter

        // Tính tổng
        val total = items.sumOf { it.price * it.quantity }
        binding.tvTotalAmount.text = "$${String.format("%.2f", total)}"

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

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rcvOrder.adapter = null
        _binding = null
    }
}
