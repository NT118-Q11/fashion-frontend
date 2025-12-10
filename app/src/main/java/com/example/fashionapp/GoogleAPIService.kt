package com.example.fashionapp

import android.content.Context

/**
 * Example class demonstrating how to use Google credentials from .env file
 */
class GoogleAPIService(private val context: Context) {

    private val clientId: String
    private val clientSecret: String

    init {
        // Initialize environment config
        EnvironmentConfig.init(context)

        // Load Google credentials from .env file
        clientId = EnvironmentConfig.getGoogleClientId()
        clientSecret = EnvironmentConfig.getGoogleClientSecret()

        // Validate credentials are loaded
        if (clientId.isEmpty()) {
            throw IllegalStateException("GOOGLE_CLIENT_ID not found in .env file")
        }

        if (clientSecret.isEmpty()) {
            throw IllegalStateException("GOOGLE_CLIENT_SECRET not found in .env file")
        }
    }

    /**
     * Get the Google Client ID
     */
    fun getClientId(): String {
        return clientId
    }

    /**
     * Get the Google Client Secret
     */
    fun getClientSecret(): String {
        return clientSecret
    }

    /**
     * Example method that might use both credentials for server-side authentication
     */
    fun authenticateWithGoogle(): Boolean {
        // Your authentication logic here
        // This is just a demonstration of how to access the credentials
        println("Using Client ID: ${clientId.take(10)}...")
        println("Using Client Secret: ${clientSecret.take(10)}...")

        // Return success/failure based on your authentication logic
        return clientId.isNotEmpty() && clientSecret.isNotEmpty()
    }
}
