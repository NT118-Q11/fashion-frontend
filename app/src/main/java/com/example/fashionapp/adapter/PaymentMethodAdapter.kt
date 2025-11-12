package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.PaymentMethod

class PaymentMethodAdapter(
    private val paymentMethods: List<PaymentMethod>,
    private val onPaymentSelected: (PaymentMethod) -> Unit
) : RecyclerView.Adapter<PaymentMethodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgIcon: ImageView = view.findViewById(R.id.imgPaymentIcon)
        val tvName: TextView = view.findViewById(R.id.tvPaymentName)
        val imgCheck: ImageView = view.findViewById(R.id.imgCheckPayment)
        val selectionBorder: View = view.findViewById(R.id.selectionBorderPayment)
        val card: View = view.findViewById(R.id.cardPayment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_method, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = paymentMethods.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val payment = paymentMethods[position]
        
        holder.imgIcon.setImageResource(payment.iconRes)
        holder.tvName.text = if (payment.cardNumber.isEmpty()) {
            payment.name
        } else {
            "${payment.name} - ${payment.cardNumber}"
        }
        
        // Show/hide selection indicators
        holder.imgCheck.visibility = if (payment.isSelected) View.VISIBLE else View.GONE
        holder.selectionBorder.visibility = if (payment.isSelected) View.VISIBLE else View.GONE
        
        // Elevate selected card
        holder.card.elevation = if (payment.isSelected) 8f else 2f

        holder.itemView.setOnClickListener {
            onPaymentSelected(payment)
        }
    }
}

