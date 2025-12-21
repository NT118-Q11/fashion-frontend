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
import com.example.fashionapp.databinding.ActivitySignInBinding
import com.example.fashionapp.data.UserManager
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    private var _binding: ActivitySignInBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleSignInManager: GoogleSignInManager
    private lateinit var userManager: UserManager

    // Activity result launcher for Google Sign-In
    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        lifecycleScope.launch {
            val signInResult = googleSignInManager.handleSignInResult(result.data)

            when (signInResult) {
                is GoogleSignInManager.GoogleSignInResult.Success -> {
                    // Call backend API with Google credentials
                    loginWithGoogle(signInResult)
                }
                is GoogleSignInManager.GoogleSignInResult.Error -> {
                    Log.e("SignInFragment", "Google Sign-In failed: ${signInResult.message}", signInResult.exception)
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
        _binding = ActivitySignInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Google Sign-In Manager
        googleSignInManager = GoogleSignInManager(requireContext())

        // Initialize UserManager
        userManager = UserManager.getInstance(requireContext())

        // Regular email/password sign in
        binding.buttonSignIn.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (validateInput(email, password)) {
                loginWithEmail(email, password)
            }
        }

        // Navigate to register
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_signInFragment_to_registerFragment)
        }

        // Google Sign-In button
        binding.buttonSignInGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter email or phone number", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter password", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    /**
     * Sign in with email/password using backend API
     */
    private fun loginWithEmail(email: String, password: String) {
        lifecycleScope.launch {
            try {
                // Backend expects 'username' field which can be email or phone number
                val request = com.example.fashionapp.UserLoginRequest(
                    username = email,  // Use email as username
                    password = password
                )
                val response = AppRoute.auth.login(request)

                if (response.user != null) {
                    // Save user data to SharedPreferences
                    userManager.saveUser(response.user)

                    Toast.makeText(
                        requireContext(),
                        "Welcome ${response.user.username ?: response.user.email}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("SignInFragment", "Email/password login failed", e)
                Log.e("SignInFragment", "Exception type: ${e.javaClass.simpleName}")
                Log.e("SignInFragment", "Exception message: ${e.message}")
                e.printStackTrace()
                Toast.makeText(
                    requireContext(),
                    "Login failed: ${e.message}",
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
     * Login with Google using backend API
     */
    private fun loginWithGoogle(result: GoogleSignInManager.GoogleSignInResult.Success) {
        lifecycleScope.launch {
            try {
                Log.d("SignInFragment", "Creating GoogleOAuth2UserInfo with token length: ${result.idToken.length}")

                val googleUserInfo = GoogleOAuth2UserInfo(
                    accessToken = result.idToken, // Backend expects "accessToken" field name
                    email = result.email,
                    name = result.name,
                    picture = result.photoUrl,
                    id = result.email // Use email as ID instead of parsing token
                )

                Log.d("SignInFragment", "GoogleOAuth2UserInfo created successfully")

                // Call backend login-gmail endpoint
                val response = AppRoute.auth.loginWithGoogle(googleUserInfo)

                if (response.user != null) {
                    // Save user data to SharedPreferences
                    // Backend now returns firstName and lastName in UserDto
                    userManager.saveUser(response.user)

                    Toast.makeText(
                        requireContext(),
                        "Welcome ${response.user.username ?: response.user.email}!",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Navigate to home
                    findNavController().navigate(R.id.action_signInFragment_to_homeFragment)
                } else {
                    Toast.makeText(
                        requireContext(),
                        response.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("SignInFragment", "Google login failed", e)
                Log.e("SignInFragment", "Exception type: ${e.javaClass.simpleName}")
                Log.e("SignInFragment", "Exception message: ${e.message}")
                Log.e("SignInFragment", "Exception cause: ${e.cause}")
                e.printStackTrace()

                // Additional logging for specific user info
                Log.d("SignInFragment", "Google user info - Email: ${result.email}, Name: ${result.name}")
                Log.d("SignInFragment", "ID Token length: ${result.idToken.length}")

                Toast.makeText(
                    requireContext(),
                    "Google login failed: ${e.message}",
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