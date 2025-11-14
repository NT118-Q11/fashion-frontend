package com.example.fashionapp.uix

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.adapter.DeliveryMethodAdapter
import com.example.fashionapp.product.DeliveryMethod
import com.google.android.material.button.MaterialButton

class DeliveryMethodActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDone: MaterialButton
    private lateinit var adapter: DeliveryMethodAdapter

    private val deliveryMethods = mutableListOf<DeliveryMethod>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delivery_method)

        initViews()
        loadDeliveryMethods()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackDelivery)
        recyclerView = findViewById(R.id.recyclerDeliveryMethods)
        btnDone = findViewById(R.id.btnDone)
    }

    private fun loadDeliveryMethods() {
        deliveryMethods.add(
            DeliveryMethod(
                1,
                "Standard Shipping",
                0,
                "(Shipping 4-6 working days)",
                true
            )
        )
        deliveryMethods.add(
            DeliveryMethod(
                2,
                "Next day",
                30,
                "(Shipping 4-6 working days)",
                false
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = DeliveryMethodAdapter(deliveryMethods) { selectedMethod ->
            deliveryMethods.forEach { it.isSelected = false }
            selectedMethod.isSelected = true
            adapter.notifyDataSetChanged()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        btnDone.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }
}


