package com.example.fashionapp.uix

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.fashionapp.R
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.GoogleOAuth2UserInfo
import com.example.fashionapp.GoogleSignInManager
import com.example.fashionapp.databinding.ActivityRegisterBinding
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInManager: GoogleSignInManager

    // Activity result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val signInResult = googleSignInManager.handleSignInResult(result.data)

            when (signInResult) {
                is GoogleSignInManager.GoogleSignInResult.Success -> {
                    // Call backend API with Google credentials
                    registerWithGoogle(signInResult)
                }
                is GoogleSignInManager.GoogleSignInResult.Error -> {
                    Log.e("RegisterFragment", "Google Sign-In failed: ${signInResult.message}", signInResult.exception)
                    Toast.makeText(
                        requireContext(),
                        "Google Sign-In failed: ${signInResult.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Google Sign-In Manager
        googleSignInManager = GoogleSignInManager(requireContext())

        // Navigate to sign in
        binding.tvSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_signInFragment)
        }

        // Regular registration button
        binding.buttonSignIn.setOnClickListener {
            val firstName = binding.firstName.text.toString().trim()
            val lastName = binding.lastName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()

            if (validateInput(firstName, lastName, email, password, confirmPassword)) {
                registerWithEmail(firstName, lastName, email, password)
            }
        }

        // Google Sign-In button
        binding.buttonSignInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateInput(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter your full name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password != confirmPassword) {
            Toast.makeText(requireContext(), "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Register with email/password using backend API
     */
    private fun registerWithEmail(firstName: String, lastName: String, email: String, password: String) {
        lifecycleScope.launch {
            val username = "$firstName $lastName"

            try {
                val request = com.example.fashionapp.UserRegistrationRequest(
                    username = username,
                    email = email,
                    password = password
                )

                Log.d("RegisterFragment", "Making registration request to backend...")
                Log.d("RegisterFragment", "Request data - Username: $username, Email: $email")

                val response = AppRoute.auth.register(request)

                Log.d("RegisterFragment", "Backend response received")
                Log.d("RegisterFragment", "Response message: ${response.message}")
                Log.d("RegisterFragment", "Response user: ${response.user}")

                if (response.user != null) {
                    Toast.makeText(
                        requireContext(),
                        "Registration successful! Welcome ${response.user.username}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterFragment", "Email registration failed", e)
                Log.e("RegisterFragment", "Exception type: ${e.javaClass.simpleName}")
                Log.e("RegisterFragment", "Exception message: ${e.message}")
                Log.e("RegisterFragment", "Exception cause: ${e.cause}")
                e.printStackTrace()

                // Additional logging for request details
                Log.d("RegisterFragment", "Registration request - Username: $username, Email: $email")

                Toast.makeText(
                    requireContext(),
                    "Registration failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /**
     * Launch Google Sign-In flow
     */
    private fun signInWithGoogle() {
        val signInIntent = googleSignInManager.getSignInIntent()
        googleSignInLauncher.launch(signInIntent)
    }

    /**
     * Register with Google using backend API
     */
    private fun registerWithGoogle(result: GoogleSignInManager.GoogleSignInResult.Success) {
        lifecycleScope.launch {
            try {
                Log.d("RegisterFragment", "Creating GoogleOAuth2UserInfo with token length: ${result.idToken.length}")

                val googleUserInfo = GoogleOAuth2UserInfo(
                    accessToken = result.idToken, // Backend expects "accessToken" field name
                    email = result.email,
                    name = result.name,
                    picture = result.photoUrl,
                    id = result.email // Use email as ID instead of parsing token
                )

                Log.d("RegisterFragment", "GoogleOAuth2UserInfo created successfully")
                Log.d("RegisterFragment", "Sending Google OAuth2 request to backend...")
                Log.d("RegisterFragment", "Request accessToken length: ${googleUserInfo.accessToken.length}")
                Log.d("RegisterFragment", "Request email: ${googleUserInfo.email}")
                Log.d("RegisterFragment", "Request name: ${googleUserInfo.name}")

                // Call backend register-gmail endpoint
                val response = AppRoute.auth.registerWithGoogle(googleUserInfo)

                Log.d("RegisterFragment", "Google registration response received")
                Log.d("RegisterFragment", "Response message: ${response.message}")
                Log.d("RegisterFragment", "Response user: ${response.user}")

                if (response.user != null) {
                    Toast.makeText(
                        requireContext(),
                        "Registration successful! Welcome ${response.user.username ?: response.user.email}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("RegisterFragment", "Google registration failed", e)
                Log.e("RegisterFragment", "Exception type: ${e.javaClass.simpleName}")
                Log.e("RegisterFragment", "Exception message: ${e.message}")
                Log.e("RegisterFragment", "Exception cause: ${e.cause}")
                e.printStackTrace()

                // Additional logging for specific user info
                Log.d("RegisterFragment", "Google user info - Email: ${result.email}, Name: ${result.name}")
                Log.d("RegisterFragment", "ID Token length: ${result.idToken.length}")

                Toast.makeText(
                    requireContext(),
                    "Google registration failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
