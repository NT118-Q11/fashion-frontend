package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import com.example.fashionapp.databinding.ActivitySearchViewBinding

class ActivitySearchViewFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<View>(R.id.btn_back)?.setOnClickListener { findNavController().navigateUp() }
        view.findViewById<View>(R.id.btn_close)?.setOnClickListener { /* no-op */ }
        view.findViewById<View>(R.id.navHome)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_homeFragment)
        }

        view.findViewById<View>(R.id.navProfile)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_myAccountFragment)
        }

        view.findViewById<View>(R.id.navCart)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_cartFragment)
        }

        view.findViewById<View>(R.id.navNotifications)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_notificationFragment)
        }


        val recycler = view.findViewById<RecyclerView>(R.id.rcv_search_results)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        val items = List(8) { index -> getString(R.string.sample_product_name) + " #${index + 1}" }
        recycler.adapter = SearchResultAdapter(items) {
            findNavController().navigate(R.id.action_activitySearchViewFragment_to_detailsFragment)
        }
    }

    private inner class SearchResultAdapter(
        private val items: List<String>,
        private val onClick: () -> Unit
    ) : RecyclerView.Adapter<SearchResultAdapter.VH>() {

        inner class VH(itemView: View, onClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
            private val title: TextView? = itemView.findViewById(R.id.txtTitle)
            private val name: TextView? = itemView.findViewById(R.id.txtName)
            private val price: TextView? = itemView.findViewById(R.id.txtPrice)
            init {
                itemView.setOnClickListener { onClick() }
            }
            fun bind(text: String) {
                title?.text = text
                name?.text = getString(R.string.sample_product_name)
                price?.text = getString(R.string.price_format, 120)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_result, parent, false)
            return VH(view, onClick)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size


    }

}
