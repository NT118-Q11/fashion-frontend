package com.example.fashionapp.uix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ActivityForgotPasswordBinding

class ForgotPasswordFragment : Fragment() {

    private var _binding: ActivityForgotPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nút Continue (tạm thời chưa điều hướng)
        binding.btnContinue.setOnClickListener {
            // TODO: Chuyển sang màn hình xác thực OTP nếu bạn có
            // findNavController().navigate(R.id.action_forgotPasswordFragment_to_verifyOtpFragment)
        }

        // Nút "Not now" → quay về SignInFragment
        binding.tvNotNow.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
