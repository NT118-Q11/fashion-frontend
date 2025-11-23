package com.example.fashionapp.uix

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fashionapp.R

class ActivitySearchViewFragment : Fragment() {

    private lateinit var recycler: RecyclerView
    private lateinit var pageButtons: List<TextView>
    private lateinit var pagePrev: ImageView
    private lateinit var pageNext: ImageView

    private var currentPage = 1
    private val itemsPerPage = 4
    private lateinit var items: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<View>(R.id.btn_back)?.setOnClickListener {
            findNavController().navigateUp()
        }
        view.findViewById<View>(R.id.btn_close)?.setOnClickListener {
            // no-op
        }

        recycler = view.findViewById(R.id.rcv_search_results)
        recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        items = List(20) { index -> getString(R.string.sample_product_name) + " #${index + 1}" }

        // Khởi tạo các nút phân trang
        pageButtons = listOf(
            view.findViewById(R.id.page_1),
            view.findViewById(R.id.page_2),
            view.findViewById(R.id.page_3),
            view.findViewById(R.id.page_4),
            view.findViewById(R.id.page_5)
        )
        pagePrev = view.findViewById(R.id.page_prev)
        pageNext = view.findViewById(R.id.page_next)

        pagePrev.setOnClickListener {
            if (currentPage > 1) {
                currentPage--
                updatePageUI()
            }
        }

        pageNext.setOnClickListener {
            if (currentPage < pageButtons.size) {
                currentPage++
                updatePageUI()
            }
        }

        pageButtons.forEachIndexed { index, button ->
            button.setOnClickListener {
                currentPage = index + 1
                updatePageUI()
            }
        }

        updatePageUI()
    }

    private fun updatePageUI() {
        pageButtons.forEachIndexed { index, button ->
            val isSelected = (index + 1) == currentPage
            button.setBackgroundResource(
                if (isSelected) R.drawable.page_selected_bg else R.drawable.page_unselected_bg
            )
            button.setTextColor(
                if (isSelected) Color.WHITE else Color.BLACK
            )
        }

        val startIndex = (currentPage - 1) * itemsPerPage
        val endIndex = minOf(startIndex + itemsPerPage, items.size)
        val pageItems = items.subList(startIndex, endIndex)

        recycler.adapter = SearchResultAdapter(pageItems) {
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
