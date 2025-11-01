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
        setContentView(R.xml.cart)

        recyclerView = findViewById(R.id.recyclerCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)

        // Thiết lập RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // ⚙️ Giả lập dữ liệu giỏ hàng (sinh viên có thể thay bằng dữ liệu thật sau)
        val cartItems = listOf("Áo thun", "Quần jeans", "Giày thể thao")

        // Tính tổng đơn giản
        tvTotal.text = "Tổng: 950.000 VNĐ"

        // Bắt sự kiện nút thanh toán
        btnCheckout.setOnClickListener {
            Toast.makeText(this, "Thanh toán thành công 🎉", Toast.LENGTH_SHORT).show()
        }
    }
}
