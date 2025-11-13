package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.ShippingAddress

class ShippingAddressAdapter(
    private val addresses: List<ShippingAddress>,
    private val onAddressSelected: (ShippingAddress) -> Unit
) : RecyclerView.Adapter<ShippingAddressAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAddressMain: TextView = view.findViewById(R.id.tvAddressMain)
        val tvAddressContact: TextView = view.findViewById(R.id.tvAddressContact)
        val imgCheck: ImageView = view.findViewById(R.id.imgCheckAddress)
        val selectionBorder: View = view.findViewById(R.id.selectionBorderAddress)
        val card: View = view.findViewById(R.id.cardAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_shipping_address, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = addresses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val address = addresses[position]
        
        holder.tvAddressMain.text = address.address
        holder.tvAddressContact.text = "${address.name} - ${address.phone}"
        
        // Show/hide selection indicators
        holder.imgCheck.visibility = if (address.isSelected) View.VISIBLE else View.GONE
        holder.selectionBorder.visibility = if (address.isSelected) View.VISIBLE else View.GONE
        
        // Elevate selected card
        holder.card.elevation = if (address.isSelected) 8f else 2f

        holder.itemView.setOnClickListener {
            onAddressSelected(address)
        }
    }
}

