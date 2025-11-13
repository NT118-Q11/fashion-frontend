package com.example.fashionapp.uix

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.adapter.CartAdapter
import com.example.fashionapp.product.CartItem
import com.google.android.material.button.MaterialButton
import kotlin.random.Random

class CheckoutSummaryActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var layoutCustomerInfo: LinearLayout
    private lateinit var layoutPaymentInfo: LinearLayout
    private lateinit var tvCustomerName: TextView
    private lateinit var tvCustomerAddress: TextView
    private lateinit var tvCustomerPhone: TextView
    private lateinit var imgPaymentIcon: ImageView
    private lateinit var tvPaymentSummary: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnFinalCheckout: MaterialButton
    
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_summary)

        initViews()
        loadData()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackCheckout)
        layoutCustomerInfo = findViewById(R.id.layoutCustomerInfo)
        layoutPaymentInfo = findViewById(R.id.layoutPaymentInfo)
        tvCustomerName = findViewById(R.id.tvCustomerName)
        tvCustomerAddress = findViewById(R.id.tvCustomerAddress)
        tvCustomerPhone = findViewById(R.id.tvCustomerPhone)
        imgPaymentIcon = findViewById(R.id.imgPaymentIconSummary)
        tvPaymentSummary = findViewById(R.id.tvPaymentSummary)
        recyclerView = findViewById(R.id.recyclerCheckoutItems)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnFinalCheckout = findViewById(R.id.btnFinalCheckout)
    }

    private fun loadData() {
        // Load customer info
        tvCustomerName.text = "Iris Watson"
        tvCustomerAddress.text = "606-3727 Ullamcorper. Street\nRoseville NH 11523"
        tvCustomerPhone.text = "(786) 713-8616"

        // Load payment info
        tvPaymentSummary.text = "Master Card ending ••••89"

        // Load cart items
        cartItems.add(
            CartItem(
                1,
                "LAMEREI",
                "Recycle Boucle Knit Cardigan Pink",
                120,
                R.drawable.dress_flower,
                1
            )
        )
        
        updateTotal()
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            cartItems,
            onQuantityChanged = {
                updateTotal()
            },
            onItemRemoved = {
                if (cartItems.isEmpty()) {
                    finish()
                } else {
                    updateTotal()
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cartAdapter
    }

    private fun updateTotal() {
        val total = cartAdapter.getTotalPrice()
        tvTotalAmount.text = "$$total"
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        
        btnFinalCheckout.setOnClickListener {
            showPaymentSuccessDialog()
        }
    }

    private fun showPaymentSuccessDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_payment_success)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(true)

        val btnClose = dialog.findViewById<ImageView>(R.id.btnCloseDialog)
        val tvPaymentId = dialog.findViewById<TextView>(R.id.tvPaymentId)
        val imgSad = dialog.findViewById<ImageView>(R.id.imgRatingSad)
        val imgNeutral = dialog.findViewById<ImageView>(R.id.imgRatingNeutral)
        val imgHappy = dialog.findViewById<ImageView>(R.id.imgRatingHappy)
        val btnSubmit = dialog.findViewById<MaterialButton>(R.id.btnSubmitRating)
        val btnBackToHome = dialog.findViewById<MaterialButton>(R.id.btnBackToHome)

        // Generate random payment ID
        val paymentId = Random.nextInt(10000000, 99999999)
        tvPaymentId.text = "Payment ID $paymentId"

        var selectedRating = 0

        // Rating selection
        imgSad.setOnClickListener {
            selectedRating = 1
            imgSad.alpha = 1.0f
            imgNeutral.alpha = 0.5f
            imgHappy.alpha = 0.5f
        }

        imgNeutral.setOnClickListener {
            selectedRating = 2
            imgSad.alpha = 0.5f
            imgNeutral.alpha = 1.0f
            imgHappy.alpha = 0.5f
        }

        imgHappy.setOnClickListener {
            selectedRating = 3
            imgSad.alpha = 0.5f
            imgNeutral.alpha = 0.5f
            imgHappy.alpha = 1.0f
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        btnSubmit.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnBackToHome.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, Home::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
}


