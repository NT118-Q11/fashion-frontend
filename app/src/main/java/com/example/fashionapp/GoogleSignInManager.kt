package com.example.fashionapp

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Google Sign-In Manager - handles Google authentication flow
 */
@Suppress("DEPRECATION")
class GoogleSignInManager(private val context: Context) {

    private val googleSignInClient: GoogleSignInClient

    init {
        println("GoogleSignInManager: Initializing...")

        // Initialize environment config with retry mechanism
        try {
            EnvironmentConfig.init(context)
            println("GoogleSignInManager: Environment config initialized")
        } catch (e: Exception) {
            println("GoogleSignInManager: Error initializing environment config: ${e.message}")
            e.printStackTrace()
        }

        // Get Google Client ID from environment variables with retry
        var clientId = EnvironmentConfig.getGoogleClientId()
        println("GoogleSignInManager: Retrieved clientId = '$clientId'")

        // If empty, try reinitializing once more
        if (clientId.isEmpty()) {
            println("GoogleSignInManager: First attempt failed, trying to reinitialize environment config...")
            try {
                // Force reinitialize by setting properties to null first
                EnvironmentConfig.forceReinit(context)
                clientId = EnvironmentConfig.getGoogleClientId()
                println("GoogleSignInManager: After reinit, clientId = '$clientId'")
            } catch (e: Exception) {
                println("GoogleSignInManager: Reinit failed: ${e.message}")
                e.printStackTrace()
            }
        }

        if (clientId.isEmpty()) {
            println("GoogleSignInManager: ERROR - GOOGLE_CLIENT_ID is still empty after retry!")
            throw IllegalStateException("GOOGLE_CLIENT_ID not found in .env file. Please check that the .env file exists in assets folder and contains the GOOGLE_CLIENT_ID.")
        }

        println("GoogleSignInManager: Using clientId for Google Sign-In configuration")

        // Configure Google Sign-In options with loaded client ID
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .requestProfile()
            .build()

        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    /**
     * Get the sign-in intent to launch
     */
    fun getSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    /**
     * Handle the sign-in result from the intent
     */
    suspend fun handleSignInResult(data: Intent?): GoogleSignInResult {
        return withContext(Dispatchers.IO) {
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    GoogleSignInResult.Success(
                        idToken = account.idToken ?: "",
                        email = account.email ?: "",
                        name = account.displayName,
                        photoUrl = account.photoUrl?.toString()
                    )
                } else {
                    GoogleSignInResult.Error("Account is null")
                }
            } catch (e: ApiException) {
                GoogleSignInResult.Error("Sign in failed: ${e.message}")
            } catch (e: Exception) {
                GoogleSignInResult.Error("Unexpected error: ${e.message}")
            }
        }
    }

    /**
     * Sign out from Google
     */
    suspend fun signOut() {
        withContext(Dispatchers.IO) {
            googleSignInClient.signOut()
        }
    }

    /**
     * Revoke access (disconnect account)
     */
    suspend fun revokeAccess() {
        withContext(Dispatchers.IO) {
            googleSignInClient.revokeAccess()
        }
    }

    /**
     * Check if user is already signed in
     */
    fun getLastSignedInAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    sealed class GoogleSignInResult {
        data class Success(
            val idToken: String,
            val email: String,
            val name: String?,
            val photoUrl: String?
        ) : GoogleSignInResult()

        data class Error(val message: String) : GoogleSignInResult()
    }
}

