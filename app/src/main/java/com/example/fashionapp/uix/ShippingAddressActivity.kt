package com.example.fashionapp.uix

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.adapter.ShippingAddressAdapter
import com.example.fashionapp.product.ShippingAddress
import com.google.android.material.button.MaterialButton

class ShippingAddressActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var btnAdd: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnDone: MaterialButton
    private lateinit var adapter: ShippingAddressAdapter

    private val addresses = mutableListOf<ShippingAddress>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_address)

        initViews()
        loadAddresses()
        setupRecyclerView()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackAddress)
        btnAdd = findViewById(R.id.btnAddAddress)
        recyclerView = findViewById(R.id.recyclerAddresses)
        btnDone = findViewById(R.id.btnDoneAddress)
    }

    private fun loadAddresses() {
        addresses.add(
            ShippingAddress(
                1,
                "Francis Delgado",
                "57 Naci Terrace, Hunidpis",
                "755 707 1486",
                false
            )
        )
        addresses.add(
            ShippingAddress(
                2,
                "Brian Griffin",
                "21 Prince, Singapore, AR 719",
                "029 105 810",
                true
            )
        )
        addresses.add(
            ShippingAddress(
                3,
                "Roger Lyons",
                "142 Reda View, Lipobdad",
                "472 471 1925",
                false
            )
        )
        addresses.add(
            ShippingAddress(
                4,
                "Ricardo Higgins",
                "824 Kihoj Pike, Ickuhiw",
                "409 828 2536",
                false
            )
        )
        addresses.add(
            ShippingAddress(
                5,
                "Nettie Gordon",
                "406 Mihe Ridge, Baijeze",
                "741 848 9295",
                false
            )
        )
    }

    private fun setupRecyclerView() {
        adapter = ShippingAddressAdapter(addresses) { selectedAddress ->
            addresses.forEach { it.isSelected = false }
            selectedAddress.isSelected = true
            adapter.notifyDataSetChanged()
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    companion object {
        const val REQUEST_ADD_ADDRESS = 100
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }
        
        btnAdd.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivityForResult(intent, REQUEST_ADD_ADDRESS)
        }
        
        btnDone.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == REQUEST_ADD_ADDRESS && resultCode == RESULT_OK) {
            // Address was added successfully
            // Here you would typically reload the address list
            Toast.makeText(this, "New address added successfully!", Toast.LENGTH_SHORT).show()
        }
    }
}

