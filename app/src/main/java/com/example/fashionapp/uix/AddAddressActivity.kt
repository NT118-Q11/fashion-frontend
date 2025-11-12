package com.example.fashionapp.uix

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatCheckBox
import com.example.fashionapp.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class AddAddressActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etFullName: TextInputEditText
    private lateinit var etPhoneNumber: TextInputEditText
    private lateinit var etStreetAddress: TextInputEditText
    private lateinit var etCity: TextInputEditText
    private lateinit var etState: TextInputEditText
    private lateinit var etZipCode: TextInputEditText
    private lateinit var cbSetDefault: AppCompatCheckBox
    private lateinit var btnSave: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btnBackAddAddress)
        etFullName = findViewById(R.id.etFullName)
        etPhoneNumber = findViewById(R.id.etPhoneNumber)
        etStreetAddress = findViewById(R.id.etStreetAddress)
        etCity = findViewById(R.id.etCity)
        etState = findViewById(R.id.etState)
        etZipCode = findViewById(R.id.etZipCode)
        cbSetDefault = findViewById(R.id.cbSetDefault)
        btnSave = findViewById(R.id.btnSaveAddress)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener {
            finish()
        }

        btnSave.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        val fullName = etFullName.text.toString().trim()
        val phoneNumber = etPhoneNumber.text.toString().trim()
        val streetAddress = etStreetAddress.text.toString().trim()
        val city = etCity.text.toString().trim()
        val state = etState.text.toString().trim()
        val zipCode = etZipCode.text.toString().trim()

        // Validation
        if (fullName.isEmpty()) {
            etFullName.error = "Please enter full name"
            etFullName.requestFocus()
            return
        }

        if (phoneNumber.isEmpty()) {
            etPhoneNumber.error = "Please enter phone number"
            etPhoneNumber.requestFocus()
            return
        }

        if (streetAddress.isEmpty()) {
            etStreetAddress.error = "Please enter street address"
            etStreetAddress.requestFocus()
            return
        }

        if (city.isEmpty()) {
            etCity.error = "Please enter city"
            etCity.requestFocus()
            return
        }

        if (state.isEmpty()) {
            etState.error = "Please enter state"
            etState.requestFocus()
            return
        }

        if (zipCode.isEmpty()) {
            etZipCode.error = "Please enter zip code"
            etZipCode.requestFocus()
            return
        }

        // Build complete address
        val completeAddress = "$streetAddress, $city, $state $zipCode"
        val isDefault = cbSetDefault.isChecked

        // Here you would save the address to your database/API
        // For now, we'll just show a success message and return
        
        Toast.makeText(
            this,
            "Address saved successfully!\n$fullName\n$completeAddress\n$phoneNumber",
            Toast.LENGTH_LONG
        ).show()

        // Return success result
        setResult(RESULT_OK)
        finish()
    }
}


