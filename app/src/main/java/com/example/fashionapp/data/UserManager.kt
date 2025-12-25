package com.example.fashionapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.fashionapp.UserDto

/**
 * UserManager handles storing and retrieving user information
 * using SharedPreferences for persistence across app sessions.
 */
class UserManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_prefs"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE_NUMBER = "phone_number"
        private const val KEY_USER_ADDRESS = "user_address"
        private const val KEY_FIRST_NAME = "first_name"
        private const val KEY_LAST_NAME = "last_name"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"

        @Volatile
        private var instance: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Save user information after successful login/register
     */
    fun saveUser(user: UserDto, firstName: String? = null, lastName: String? = null) {
        prefs.edit().apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USERNAME, user.username)
            putString(KEY_EMAIL, user.email)
            putString(KEY_PHONE_NUMBER, user.phoneNumber)
            putString(KEY_USER_ADDRESS, user.userAddress)

            // Use firstName/lastName from UserDto if available, otherwise use provided parameters
            // Only save non-empty strings, let empty values be null
            val finalFirstName = user.firstName?.takeIf { it.isNotBlank() }
                ?: firstName?.takeIf { it.isNotBlank() }
            val finalLastName = user.lastName?.takeIf { it.isNotBlank() }
                ?: lastName?.takeIf { it.isNotBlank() }

            putString(KEY_FIRST_NAME, finalFirstName)
            putString(KEY_LAST_NAME, finalLastName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }

        // Update FavoritesManager with user ID
        FavoritesManager.getInstance(context).setUserId(user.id)
    }

    /**
     * Get current logged in user
     */
    fun getUser(): UserDto? {
        if (!isLoggedIn()) return null

        return UserDto(
            id = prefs.getString(KEY_USER_ID, null),
            username = prefs.getString(KEY_USERNAME, null),
            email = prefs.getString(KEY_EMAIL, null),
            phoneNumber = prefs.getString(KEY_PHONE_NUMBER, null),
            userAddress = prefs.getString(KEY_USER_ADDRESS, null)
        )
    }

    /**
     * Get user's first name
     */
    fun getFirstName(): String? {
        return prefs.getString(KEY_FIRST_NAME, null)
    }

    /**
     * Get user's last name
     */
    fun getLastName(): String? {
        return prefs.getString(KEY_LAST_NAME, null)
    }

    /**
     * Update user profile information
     */
    fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null,
        phoneNumber: String? = null,
        address: String? = null
    ) {
        prefs.edit().apply {
            firstName?.let { putString(KEY_FIRST_NAME, it) }
            lastName?.let { putString(KEY_LAST_NAME, it) }
            email?.let { putString(KEY_EMAIL, it) }
            phoneNumber?.let { putString(KEY_PHONE_NUMBER, it) }
            address?.let { putString(KEY_USER_ADDRESS, it) }
            apply()
        }
    }

    /**
     * Check if user is logged in
     */
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Logout user and clear all data
     */
    fun logout() {
        prefs.edit().clear().apply()
        // Clear FavoritesManager userId
        FavoritesManager.getInstance(context).setUserId(null)
    }

    /**
     * Get user email
     */
    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    /**
     * Get user phone number
     */
    fun getPhoneNumber(): String? {
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }

    /**
     * Get user address
     */
    fun getUserAddress(): String? {
        return prefs.getString(KEY_USER_ADDRESS, null)
    }

    /**
     * Get username
     */
    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    /**
     * Save/Update user address
     */
    fun saveAddress(address: String) {
        prefs.edit().apply {
            putString(KEY_USER_ADDRESS, address)
            apply()
        }
    }

    /**
     * Get user ID
     */
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
}

