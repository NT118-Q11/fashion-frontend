package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.adapter.CartAdapter
import com.example.fashionapp.adapter.ConfirmAdapter
import com.example.fashionapp.databinding.ActivityConfirmBinding

class ConfirmFragment : Fragment() {

    private var _binding: ActivityConfirmBinding? = null
    private val binding get() = _binding!!

    data class CartItem(
        val id: Int,
        val title: String,
        val description: String,
        val price: Double,
        val quantity: Int,
        val imageUrl: String
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityConfirmBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val demoItems = listOf(
            CartItem(
                id = 1,
                title = "LAMEREI",
                description = "Recycle Boucle Knit Cardigan Pink",
                price = 120.0,
                quantity = 1,
                imageUrl = "R.drawable.sample_woman"
            ),
            CartItem(
                id = 2,
                title = "21WN",
                description = "Reversible Angora Cardigan",
                price = 99.0,
                quantity = 2,
                imageUrl = "R.drawable.another_sample"
            )
        )

        val cartAdapter = ConfirmAdapter(demoItems)

        // Thiết lập RecyclerView
        binding.rcvOrder.apply {

            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ConfirmButton.setOnClickListener {
            findNavController().navigate(R.id.action_confirmFragment_to_paymentSuccessFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rcvOrder.adapter = null
        _binding = null
    }
}