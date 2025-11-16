package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.adapter.CartAdapter
import com.example.fashionapp.product.CartItem
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class Cart : AppCompatActivity() {

    private lateinit var emptyCartLayout: LinearLayout
    private lateinit var cartContentLayout: View
    private lateinit var bottomSection: LinearLayout
    private lateinit var recyclerCart: RecyclerView
    private lateinit var tvSubtotal: MaterialTextView
    private lateinit var btnCheckout: MaterialButton
    private lateinit var btnShoppingNow: MaterialButton
    private lateinit var btnClearAll: TextView
    
    // Navigation bar
    private lateinit var navHome: ImageView
    private lateinit var navNotifications: ImageView
    private lateinit var navCart: ImageView
    private lateinit var navProfile: ImageView

    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart_nothing)

        initViews()
        setupRecyclerView()
        loadCartData()
        updateUI()
        setupListeners()
    }

    private fun initViews() {
        emptyCartLayout = findViewById(R.id.emptyCartLayout)
        cartContentLayout = findViewById(R.id.cartContentLayout)
        bottomSection = findViewById(R.id.bottomSection)
        recyclerCart = findViewById(R.id.recyclerCart)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnShoppingNow = findViewById(R.id.btnShoppingNow)
        btnClearAll = findViewById(R.id.btnClearAll)
        
        // Navigation bar
        navHome = findViewById(R.id.navHome)
        navNotifications = findViewById(R.id.navNotifications)
        navCart = findViewById(R.id.navCart)
        navProfile = findViewById(R.id.navProfile)
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChanged = {
                updateSubtotal()
            },
            onItemRemoved = {
                updateUI()
                if (cartItems.isEmpty()) {
                    Toast.makeText(this, "Cart is now empty", Toast.LENGTH_SHORT).show()
                }
            }
        )
        recyclerCart.apply {
            layoutManager = LinearLayoutManager(this@Cart)
            adapter = cartAdapter
        }
    }

    private fun loadCartData() {
        // Sample cart data - Replace with your actual cart data source
        // You can get this from SharedPreferences, Database, or API
        
        // Adding sample items to cart
        cartItems.add(
            CartItem(
                id = 1,
                brand = "LAMEREI",
                name = "Recycle Boucle Knit Cardigan Pink",
                price = 120,
                imageRes = R.drawable.dress_flower,
                quantity = 1
            )
        )
        
        cartItems.add(
            CartItem(
                id = 2,
                brand = "5252 BY OIOI",
                name = "2021 Signature Sweatshirt [NAVY]",
                price = 120,
                imageRes = R.drawable.shirt_white,
                quantity = 1
            )
        )
        
        // Notify adapter about the data
        cartAdapter.notifyDataSetChanged()
    }

    private fun updateUI() {
        if (cartItems.isEmpty()) {
            // Show empty cart state
            emptyCartLayout.visibility = View.VISIBLE
            cartContentLayout.visibility = View.GONE
            bottomSection.visibility = View.GONE
            btnClearAll.visibility = View.GONE
        } else {
            // Show cart with items
            emptyCartLayout.visibility = View.GONE
            cartContentLayout.visibility = View.VISIBLE
            bottomSection.visibility = View.VISIBLE
            btnClearAll.visibility = View.VISIBLE
            cartAdapter.notifyDataSetChanged()
            updateSubtotal()
        }
    }

    private fun updateSubtotal() {
        val total = cartAdapter.getTotalPrice()
        tvSubtotal.text = "$$total"
    }

    private fun setupListeners() {
        // Clear All button - removes all items from cart
        btnClearAll.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                // Show confirmation dialog
                val builder = android.app.AlertDialog.Builder(this)
                builder.setTitle("Clear Cart")
                builder.setMessage("Are you sure you want to remove all items from your cart?")
                builder.setPositiveButton("Yes") { _, _ ->
                    cartAdapter.clearAll()
                    updateUI()
                    Toast.makeText(this, "Cart cleared", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("Cancel", null)
                builder.show()
            }
        }

        // Shopping Now button - navigates to home/products
        btnShoppingNow.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        // Checkout button
        btnCheckout.setOnClickListener {
            if (cartItems.isNotEmpty()) {
                Toast.makeText(
                    this,
                    "Checkout feature - Total: $${cartAdapter.getTotalPrice()}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Bottom navigation
        navHome.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        navNotifications.setOnClickListener {
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
        }

        navCart.setOnClickListener {
            // Already on cart screen
            Toast.makeText(this, "You are on Cart", Toast.LENGTH_SHORT).show()
        }

        navProfile.setOnClickListener {
            Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
            // Navigate to profile screen
            // val intent = Intent(this, ProfileActivity::class.java)
            // startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh cart data when returning to this screen
        cartAdapter.notifyDataSetChanged()
        updateUI()
    }
}
