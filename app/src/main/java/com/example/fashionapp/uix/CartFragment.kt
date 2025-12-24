package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fashionapp.R
import com.example.fashionapp.adapter.CartAdapter
import com.example.fashionapp.data.CartManager
import com.example.fashionapp.data.UserManager
import com.example.fashionapp.databinding.ActivityCartBinding
import com.example.fashionapp.model.Cart
import kotlinx.coroutines.launch

class CartFragment : Fragment() {

    private var _binding: ActivityCartBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var userManager: UserManager
    private var cartAdapter: CartAdapter? = null
    private var currentCart: Cart? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityCartBinding.inflate(inflater, container, false)
        userManager = UserManager.getInstance(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        loadCartData()

        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_activitySearchViewFragment)
        }

        binding.btnCheckout.setOnClickListener {
            // Validate cart is not empty
            val cart = currentCart
            if (cart == null || cart.items.isEmpty()) {
                Toast.makeText(
                    context,
                    "Your cart is empty. Please add items before checkout.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Pass cart total to checkout
            val bundle = Bundle().apply {
                putDouble("cartTotal", cart.totalPrice)
            }
            findNavController().navigate(
                R.id.action_cartFragment_to_checkoutFragment,
                bundle
            )
        }

        // bottom nav
        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_homeFragment)
        }
        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_myAccountFragment)
        }
        binding.navNotifications.setOnClickListener {
            findNavController().navigate(R.id.NotificationFragment)
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            mutableListOf(),
            onIncrease = { item -> updateQuantity(item.id, item.quantity + 1) },
            onDecrease = { item -> 
                if (item.quantity > 1) {
                    updateQuantity(item.id, item.quantity - 1)
                } else {
                    removeItem(item.id)
                }
            },
            onRemove = { item -> removeItem(item.id) }
        )

        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun loadCartData() {
        val userId = userManager.getUserId() ?: return
        
        lifecycleScope.launch {
            // Note: progressBar not available in layout
            val cart = CartManager.getCart(userId)
            
            if (cart != null) {
                updateUI(cart)
            } else {
                Toast.makeText(context, "Failed to load cart", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateQuantity(itemId: String, newQuantity: Int) {
        val userId = userManager.getUserId() ?: return
        lifecycleScope.launch {
            val success = CartManager.updateQuantity(itemId, userId, newQuantity)
            if (success) {
                loadCartData() 
            } else {
                Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeItem(itemId: String) {
        val userId = userManager.getUserId() ?: return
        lifecycleScope.launch {
            val success = CartManager.removeItem(itemId, userId)
            if (success) {
                loadCartData()
            } else {
                Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(cart: Cart) {
        currentCart = cart // Lưu cart hiện tại
        cartAdapter?.updateItems(cart.items)
        binding.tvSubtotalPrice.text = "$${String.format("%.2f", cart.totalPrice)}"
        
        // Note: tvEmptyCart not available in layout
        if (cart.items.isEmpty()) {
            binding.rvCart.visibility = View.GONE
        } else {
            binding.rvCart.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
