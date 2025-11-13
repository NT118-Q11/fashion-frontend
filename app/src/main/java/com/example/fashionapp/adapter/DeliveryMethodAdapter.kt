package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.product.DeliveryMethod

class DeliveryMethodAdapter(
    private val deliveryMethods: List<DeliveryMethod>,
    private val onMethodSelected: (DeliveryMethod) -> Unit
) : RecyclerView.Adapter<DeliveryMethodAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvDeliveryName)
        val tvDescription: TextView = view.findViewById(R.id.tvDeliveryDescription)
        val imgCheck: ImageView = view.findViewById(R.id.imgCheck)
        val selectionBorder: View = view.findViewById(R.id.selectionBorder)
        val card: View = view.findViewById(R.id.cardDelivery)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_delivery_method, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = deliveryMethods.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val method = deliveryMethods[position]
        
        holder.tvName.text = if (method.price == 0) {
            "${method.name} - Free"
        } else {
            "${method.name} - $${method.price}"
        }
        holder.tvDescription.text = method.description
        
        // Show/hide selection indicators
        holder.imgCheck.visibility = if (method.isSelected) View.VISIBLE else View.GONE
        holder.selectionBorder.visibility = if (method.isSelected) View.VISIBLE else View.GONE
        
        // Elevate selected card
        holder.card.elevation = if (method.isSelected) 8f else 2f

        holder.itemView.setOnClickListener {
            onMethodSelected(method)
        }
    }
}

