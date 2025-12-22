package com.example.fashionapp

import android.content.Context
import android.content.Intent
import android.util.Log
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
                    Log.d("GoogleSignInManager", "Sign-in successful for ${account.email}")
                    Log.d("GoogleSignInManager", "ID Token present: ${account.idToken != null}")
                    Log.d("GoogleSignInManager", "Account details - Name: ${account.displayName}, Photo: ${account.photoUrl}")
                    if (account.idToken != null) {
                        Log.d("GoogleSignInManager", "ID Token length: ${account.idToken!!.length}")
                    }

                    GoogleSignInResult.Success(
                        idToken = account.idToken ?: "",
                        email = account.email ?: "",
                        name = account.displayName,
                        photoUrl = account.photoUrl?.toString()
                    )
                } else {
                    Log.e("GoogleSignInManager", "Account is null after sign-in")
                    GoogleSignInResult.Error("Account is null")
                }
            } catch (e: ApiException) {
                val errorCode = e.statusCode
                val errorMessage = when (errorCode) {
                    12500 -> "Sign-in failed: Developer error (error code 12500). Check SHA-1 certificate fingerprint in Google Cloud Console matches your debug/release keystore."
                    12501 -> "Sign-in canceled by user (error code 12501)"
                    10 -> "Sign-in failed: Developer error (error code 10). Verify OAuth 2.0 Client ID is correctly configured and requestIdToken() is using the correct Web Client ID."
                    7 -> "Network error (error code 7). Check your internet connection."
                    else -> "Sign-in failed with error code $errorCode: ${e.message}"
                }
                Log.e("GoogleSignInManager", "ApiException - $errorMessage", e)
                Log.e("GoogleSignInManager", "Error code: $errorCode")
                e.printStackTrace()
                GoogleSignInResult.Error(errorMessage, e)
            } catch (e: Exception) {
                Log.e("GoogleSignInManager", "Unexpected exception - ${e.message}", e)
                Log.e("GoogleSignInManager", "Exception type: ${e.javaClass.simpleName}")
                e.printStackTrace()
                GoogleSignInResult.Error("Unexpected error: ${e.message}", e)
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

        data class Error(val message: String, val exception: Exception? = null) : GoogleSignInResult()
    }
}
