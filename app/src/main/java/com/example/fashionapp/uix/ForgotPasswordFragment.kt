package com.example.fashionapp.uix

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.fashionapp.AppRoute
import com.example.fashionapp.R
import com.example.fashionapp.databinding.ActivityForgotPasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ForgotPasswordFragment : Fragment() {

    private var _binding: ActivityForgotPasswordBinding? = null
    private val binding get() = _binding!!

    private var emailAddress: String = ""
    private var otpSent: Boolean = false

    private lateinit var otpBoxes: List<EditText>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ActivityForgotPasswordBinding.inflate(inflater, container, false)

        // Get email from arguments if passed from SignInFragment
        emailAddress = arguments?.getString("email") ?: ""
        if (emailAddress.isNotEmpty()) {
            binding.etEmail.setText(emailAddress)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize OTP boxes list
        otpBoxes = listOf(
            binding.etOtp1,
            binding.etOtp2,
            binding.etOtp3,
            binding.etOtp4,
            binding.etOtp5,
            binding.etOtp6
        )

        setupListeners()
        setupOtpBoxes()
    }

    private fun setupOtpBoxes() {
        // Setup auto-focus between OTP boxes
        otpBoxes.forEachIndexed { index, editText ->
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s?.length == 1) {
                        // Move to next box
                        if (index < otpBoxes.size - 1) {
                            otpBoxes[index + 1].requestFocus()
                        }
                    }
                    // Hide error when user types
                    binding.tvOtpError.visibility = View.GONE
                    validateResetPasswordButton()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Handle backspace to go to previous box
            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (editText.text.isEmpty() && index > 0) {
                        otpBoxes[index - 1].requestFocus()
                        otpBoxes[index - 1].setText("")
                        return@setOnKeyListener true
                    }
                }
                false
            }
        }
    }

    private fun setupListeners() {
        // Email text change listener
        binding.etEmail.addTextChangedListener {
            binding.emailInputLayout.error = null
        }

        // New password text change listener
        binding.etNewPassword.addTextChangedListener {
            binding.newPasswordInputLayout.error = null
            validateResetPasswordButton()
        }

        // Confirm password text change listener
        binding.etConfirmPassword.addTextChangedListener {
            binding.confirmPasswordInputLayout.error = null
            validateResetPasswordButton()
        }

        // Send OTP button
        binding.btnSendOtp.setOnClickListener {
            sendOtp()
        }

        // Resend OTP
        binding.tvResend.setOnClickListener {
            resendOtp()
        }

        // Reset Password button
        binding.btnContinue.setOnClickListener {
            resetPassword()
        }

        // Not now button - go back to SignIn
        binding.tvNotNow.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment)
        }
    }

    private fun getOtpCode(): String {
        return otpBoxes.joinToString("") { it.text.toString() }
    }

    private fun clearOtpBoxes() {
        otpBoxes.forEach { it.setText("") }
        otpBoxes[0].requestFocus()
    }

    private fun sendOtp() {
        val email = binding.etEmail.text.toString().trim()

        // Validate email
        if (email.isEmpty()) {
            binding.emailInputLayout.error = "Email is required"
            return
        }

        if (!isValidGmail(email)) {
            binding.emailInputLayout.error = "Please enter a valid Gmail address"
            return
        }

        emailAddress = email

        // Show loading state
        binding.btnSendOtp.isEnabled = false
        binding.btnSendOtp.text = "Sending..."

        lifecycleScope.launch {
            try {
                // Call backend API to send OTP on IO thread
                val request = com.example.fashionapp.ForgotPasswordRequest(email)
                val response = withContext(Dispatchers.IO) {
                    AppRoute.auth.forgotPassword(request)
                }

                // Update UI on Main thread
                binding.otpSection.visibility = View.VISIBLE
                binding.btnSendOtp.visibility = View.GONE
                binding.etEmail.isEnabled = false

                otpSent = true

                Toast.makeText(
                    requireContext(),
                    response.message,
                    Toast.LENGTH_LONG
                ).show()

                // Update info text
                binding.tvSentOtp.text = "OTP sent to $email"

                // Focus on first OTP box
                otpBoxes[0].requestFocus()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to send OTP: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                binding.btnSendOtp.isEnabled = true
                binding.btnSendOtp.text = "SEND OTP"
            }
        }
    }

    private fun resendOtp() {
        if (emailAddress.isEmpty()) {
            Toast.makeText(requireContext(), "Email not found", Toast.LENGTH_SHORT).show()
            return
        }

        binding.tvResend.isEnabled = false
        clearOtpBoxes()

        lifecycleScope.launch {
            try {
                // Call backend API to resend OTP on IO thread
                val request = com.example.fashionapp.ForgotPasswordRequest(emailAddress)
                val response = withContext(Dispatchers.IO) {
                    AppRoute.auth.forgotPassword(request)
                }

                Toast.makeText(
                    requireContext(),
                    response.message,
                    Toast.LENGTH_SHORT
                ).show()

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to resend OTP: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                binding.tvResend.isEnabled = true
            }
        }
    }

    private fun resetPassword() {
        val otp = getOtpCode()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Validate inputs
        var hasError = false

        if (otp.isEmpty() || otp.length < 6) {
            binding.tvOtpError.text = "Please enter complete 6-digit OTP"
            binding.tvOtpError.visibility = View.VISIBLE
            hasError = true
        }

        if (newPassword.isEmpty()) {
            binding.newPasswordInputLayout.error = "Password is required"
            hasError = true
        } else if (newPassword.length < 6) {
            binding.newPasswordInputLayout.error = "Password must be at least 6 characters"
            hasError = true
        }

        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordInputLayout.error = "Please confirm password"
            hasError = true
        } else if (newPassword != confirmPassword) {
            binding.confirmPasswordInputLayout.error = "Passwords do not match"
            hasError = true
        }

        if (hasError) return

        // Show loading state
        binding.btnContinue.isEnabled = false
        binding.btnContinue.text = "Resetting..."

        lifecycleScope.launch {
            try {
                // Call backend API to reset password with OTP on IO thread
                val request = com.example.fashionapp.ResetPasswordRequest(emailAddress, otp, newPassword)
                val response = withContext(Dispatchers.IO) {
                    AppRoute.auth.resetPassword(request)
                }

                if (response.error != null) {
                    Toast.makeText(
                        requireContext(),
                        response.error,
                        Toast.LENGTH_LONG
                    ).show()
                    binding.btnContinue.isEnabled = true
                    binding.btnContinue.text = "RESET PASSWORD"
                    return@launch
                }

                Toast.makeText(
                    requireContext(),
                    response.message ?: "Password reset successfully!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate back to SignIn
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_signInFragment)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Failed to reset password: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.btnContinue.isEnabled = true
                binding.btnContinue.text = "RESET PASSWORD"
            }
        }
    }

    private fun validateResetPasswordButton() {
        val otp = getOtpCode()
        val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()

        // Enable button only if all fields are filled
        binding.btnContinue.isEnabled = otp.length == 6 &&
            newPassword.length >= 6 &&
            confirmPassword.isNotEmpty()
    }

    private fun isValidGmail(email: String): Boolean {
        return email.isNotEmpty() &&
            Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            email.lowercase().endsWith("@gmail.com")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
