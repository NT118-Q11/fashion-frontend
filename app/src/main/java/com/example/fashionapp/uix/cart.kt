package com.example.fashionapp.uix

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class Cart : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: MaterialTextView
    private lateinit var btnCheckout: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cart)

        recyclerView = findViewById(R.id.recyclerCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)

        recyclerView.layoutManager = LinearLayoutManager(this)

        val cartItems = listOf("Áo thun", "Quần jeans", "Giày thể thao")

        tvTotal.text = "Tổng: 950.000 VNĐ"

        btnCheckout.setOnClickListener {
            Toast.makeText(this, "Thanh toán thành công 🎉", Toast.LENGTH_SHORT).show()
        }
    }
}
