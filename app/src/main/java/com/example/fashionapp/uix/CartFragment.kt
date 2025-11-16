package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.CartAdapter
import com.example.fashionapp.databinding.ActivityCartBinding


class CartFragment : Fragment() {
    private var _binding: ActivityCartBinding? = null
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
        _binding = ActivityCartBinding.inflate(inflater, container, false)
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


        val cartAdapter = CartAdapter(demoItems)

        // Thiết lập RecyclerView
        binding.rvCart.apply {

            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }


        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
        }

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_myAccountFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvCart.adapter = null
        _binding = null
    }
}
