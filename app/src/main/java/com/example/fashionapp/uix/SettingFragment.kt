package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.SettingsBinding

class SettingFragment : Fragment() {
    private var _binding: SettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Sử dụng View Binding để inflate layout
        _binding = SettingsBinding.inflate(inflater, container, false)
        // Trả về view gốc của layout
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            // Lệnh này sẽ quay lại màn hình trước đó trong Back Stack (tức là MyAccountFragment)
            findNavController().popBackStack()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }

}