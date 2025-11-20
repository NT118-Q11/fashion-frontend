package com.example.fashionapp.uix
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivityNotificationBinding

class NotificationsFragment : Fragment() {
    private var _binding: ActivityNotificationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.navProfile.setOnClickListener {
            findNavController().navigate(R.id.action_NotificationFragment_to_myAccountFragment)
        }

        binding.navCart.setOnClickListener {
            findNavController().navigate(R.id.action_NotificationFragment_to_cartFragment)
        }

        binding.navHome.setOnClickListener {
            findNavController().navigate(R.id.action_NotificationFragment_to_homeFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Dọn dẹp binding để tránh rò rỉ bộ nhớ
        _binding = null
    }
}