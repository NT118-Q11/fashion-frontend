package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController

class ActivitySearchFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Search icon on top bar
        view.findViewById<View>(R.id.btn_filter)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_activitySearchViewFragment)
        }
        // Big center magnifying glass icon
        view.findViewById<View>(R.id.center_search_icon)?.setOnClickListener {
            findNavController().navigate(R.id.action_activitySearchFragment_to_activitySearchViewFragment)
        }
    }
}
