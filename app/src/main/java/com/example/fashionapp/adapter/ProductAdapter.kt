package com.example.fashionapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R
import com.example.fashionapp.model.Product

/**
 * Adapter for displaying products in a RecyclerView
 */
class ProductAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)
        private val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        private val txtName: TextView = itemView.findViewById(R.id.txtName)
        private val txtPrice: TextView = itemView.findViewById(R.id.txtPrice)
        private val btnFavorite: ImageView = itemView.findViewById(R.id.btnFavorite)

        fun bind(product: Product) {
            txtTitle.text = product.brand ?: "Fashion Item"
            txtName.text = product.name
            txtPrice.text = "$${String.format("%.2f", product.price)}"

            // TODO: Load image using Glide or Coil when implementing image loading
            // For now, use placeholder
            imgProduct.setImageResource(R.drawable.sample_woman)

            // Handle favorite button click
            btnFavorite.setOnClickListener {
                // TODO: Implement favorite functionality
            }

            // Handle item click
            itemView.setOnClickListener {
                onItemClick(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount(): Int = products.size

    /**
     * Update the product list and refresh the RecyclerView
     */
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        notifyDataSetChanged()
    }
}

