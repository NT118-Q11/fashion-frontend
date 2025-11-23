package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.net.Uri
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.databinding.ActivityWelcomeBinding

class WelcomeFragment : Fragment() {
    private var _binding: ActivityWelcomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Video background ---
        val videoUri = Uri.parse("android.resource://${requireContext().packageName}/${R.raw.welcome_bg}")
        binding.backgroundVideo.setVideoURI(videoUri)

        binding.backgroundVideo.setOnPreparedListener { mp ->
            mp.isLooping = true
            mp.setVolume(0f, 0f) // tắt tiếng
            binding.backgroundVideo.start()
        }

        // Điều hướng
        binding.buttonSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signInFragment)
        }

        binding.buttonSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_registerFragment)
        }
    }

    override fun onPause() {
        super.onPause()
        binding.backgroundVideo.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.backgroundVideo.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
