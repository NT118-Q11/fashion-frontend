package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivitySignInBinding

class SignInFragment : Fragment() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivitySignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
        }

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}