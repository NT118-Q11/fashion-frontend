package com.example.fashionapp.uix

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.adapter.PaymentMethodAdapter
import com.example.fashionapp.product.PaymentMethod
import com.google.android.material.button.MaterialButton

class PaymentMethodActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnAdd: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDone: MaterialButton
    private lateinit var adapter: PaymentMethodAdapter

    private val paymentMethods = mutableListOf<PaymentMethod>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_method)

        initViews()
        loadPaymentMethods()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackPayment)
        btnAdd = findViewById(R.id.btnAddPayment)
        recyclerView = findViewById(R.id.recyclerPaymentMethods)
        btnDone = findViewById(R.id.btnDonePayment)
    }

    private fun loadPaymentMethods() {
        paymentMethods.add(
            PaymentMethod(
                1,
                "Visa",
                "•••• •••• 2940",
                R.drawable.ic_launcher_foreground,
                true
            )
        )
        paymentMethods.add(
            PaymentMethod(
                2,
                "Paypal",
                "",
                R.drawable.ic_launcher_foreground,
                false
            )
        )
        paymentMethods.add(
            PaymentMethod(
                3,
                "Master Card",
                "",
                R.drawable.ic_launcher_foreground,
                false
            )
        )
        paymentMethods.add(
            PaymentMethod(
                4,
                "Apple Pay",
                "",
                R.drawable.ic_launcher_foreground,
                false
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = PaymentMethodAdapter(paymentMethods) { selectedPayment ->
            paymentMethods.forEach { it.isSelected = false }
            selectedPayment.isSelected = true
            adapter.notifyDataSetChanged()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnAdd.setOnClickListener {
            Toast.makeText(this, "Add new payment method", Toast.LENGTH_SHORT).show()
        }
        btnDone.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}


