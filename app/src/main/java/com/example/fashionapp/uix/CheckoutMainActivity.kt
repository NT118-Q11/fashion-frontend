package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fashionapp.R
import com.example.fashionapp.product.DeliveryMethod
import com.example.fashionapp.product.PaymentMethod
import com.example.fashionapp.product.ShippingAddress
import com.google.android.material.button.MaterialButton

class CheckoutMainActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var layoutDeliveryMethod: LinearLayout
    private lateinit var layoutShippingAddress: LinearLayout
    private lateinit var layoutPaymentMethod: LinearLayout
    private lateinit var tvDeliveryMethod: TextView
    private lateinit var tvShippingAddress: TextView
    private lateinit var tvPaymentMethod: TextView
    private lateinit var btnCheckoutMain: MaterialButton
    private lateinit var btnPay: MaterialButton

    private var selectedDelivery: DeliveryMethod? = null
    private var selectedAddress: ShippingAddress? = null
    private var selectedPayment: PaymentMethod? = null

    companion object {
        const val REQUEST_DELIVERY = 1
        const val REQUEST_ADDRESS = 2
        const val REQUEST_PAYMENT = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_main)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBack)
        layoutDeliveryMethod = findViewById(R.id.layoutDeliveryMethod)
        layoutShippingAddress = findViewById(R.id.layoutShippingAddress)
        layoutPaymentMethod = findViewById(R.id.layoutPaymentMethod)
        tvDeliveryMethod = findViewById(R.id.tvDeliveryMethod)
        tvShippingAddress = findViewById(R.id.tvShippingAddress)
        tvPaymentMethod = findViewById(R.id.tvPaymentMethod)
        btnCheckoutMain = findViewById(R.id.btnCheckoutMain)
        btnPay = findViewById(R.id.btnPay)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        layoutDeliveryMethod.setOnClickListener {
            val intent = Intent(this, DeliveryMethodActivity::class.java)
            startActivityForResult(intent, REQUEST_DELIVERY)
        }

        layoutShippingAddress.setOnClickListener {
            val intent = Intent(this, ShippingAddressActivity::class.java)
            startActivityForResult(intent, REQUEST_ADDRESS)
        }

        layoutPaymentMethod.setOnClickListener {
            val intent = Intent(this, PaymentMethodActivity::class.java)
            startActivityForResult(intent, REQUEST_PAYMENT)
        }

        btnCheckoutMain.setOnClickListener {
            proceedToCheckout()
        }

        btnPay.setOnClickListener {
            proceedToCheckout()
        }
    }

    private fun proceedToCheckout() {
        if (selectedDelivery == null) {
            Toast.makeText(this, "Please select a delivery method", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedAddress == null) {
            Toast.makeText(this, "Please select a shipping address", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedPayment == null) {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CheckoutSummaryActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        when (requestCode) {
            REQUEST_DELIVERY -> {
                // Update delivery method text
                tvDeliveryMethod.text = "Standard Shipping - Free"
                tvDeliveryMethod.setTextColor(resources.getColor(android.R.color.black, null))
                selectedDelivery = DeliveryMethod(1, "Standard Shipping", 0, "4-6 days", true)
            }
            REQUEST_ADDRESS -> {
                // Update address text
                tvShippingAddress.text = "21 Prince, Singapore, AR 719"
                tvShippingAddress.setTextColor(resources.getColor(android.R.color.black, null))
                selectedAddress = ShippingAddress(1, "Brian Griffin", "21 Prince, Singapore, AR 719", "029 105 810", true)
            }
            REQUEST_PAYMENT -> {
                // Update payment text
                tvPaymentMethod.text = "Visa - •••• •••• 2940"
                tvPaymentMethod.setTextColor(resources.getColor(android.R.color.black, null))
                selectedPayment = PaymentMethod(1, "Visa", "•••• •••• 2940", R.drawable.ic_launcher_foreground, true)
            }
        }
    }
}


