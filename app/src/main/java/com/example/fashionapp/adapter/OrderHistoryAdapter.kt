package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.model.OrderResponse
import java.text.SimpleDateFormat
import java.util.*

class OrderHistoryAdapter(
    private var orders: List<OrderResponse>
) : RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder>() {

    private var onItemClickListener: ((OrderResponse) -> Unit)? = null

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvOrderStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvOrderTotal: TextView = itemView.findViewById(R.id.tvTotal)

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.invoke(orders[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_order_history, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.tvOrderId.text = "#${order.id?.take(8) ?: "N/A"}"
        holder.tvOrderDate.text = formatDate(order.createdAt)
        holder.tvOrderStatus.text = order.status
        holder.tvOrderTotal.text = "$${String.format("%.2f", order.totalAmount)}"

        // Set status color based on status
        val context = holder.itemView.context
        when (order.status.uppercase()) {
            "DELIVERED" -> holder.tvOrderStatus.setTextColor(context.getColor(R.color.green))
            "CANCELLED" -> holder.tvOrderStatus.setTextColor(context.getColor(R.color.red))
            "PENDING" -> holder.tvOrderStatus.setTextColor(context.getColor(R.color.orange))
            else -> holder.tvOrderStatus.setTextColor(context.getColor(R.color.black))
        }
    }

    override fun getItemCount() = orders.size

    fun updateOrders(newOrders: List<OrderResponse>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(listener: (OrderResponse) -> Unit) {
        onItemClickListener = listener
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return "N/A"
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}

