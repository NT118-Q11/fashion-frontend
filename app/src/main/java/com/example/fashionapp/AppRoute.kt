package com.example.fashionapp

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes for requests
data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    val phone_number: String? = null,
    val user_address: String? = null
)

data class UserLoginRequest(
    val email: String,
    val password: String
)

// Minimal Google OAuth2 user info expected by backend
data class GoogleOAuth2UserInfo(
    val idToken: String, // Backend sẽ dùng token này để xác thực
    val email: String,
    val name: String? = null,
    val picture: String? = null
)

// User DTO matching backend response
data class UserDto(
    val id: Long,
    val username: String?,
    val email: String?,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    @SerializedName("user_address")
    val userAddress: String? = null
)

// Generic API response wrapper used by the backend
data class ApiResponse<T>(
    val message: String,
    val user: T?
)

// Retrofit API definitions
interface AuthApi {
    @POST("/api/auth/register")
    suspend fun register(@Body request: UserRegistrationRequest): ApiResponse<UserDto>

    @POST("/api/auth/login")
    suspend fun login(@Body request: UserLoginRequest): ApiResponse<UserDto>

    @POST("/api/auth/register-gmail")
    suspend fun registerWithGoogle(@Body info: GoogleOAuth2UserInfo): ApiResponse<UserDto>

    @POST("/api/auth/login-gmail")
    suspend fun loginWithGoogle(@Body info: GoogleOAuth2UserInfo): ApiResponse<UserDto>
}

// AppRoute object to provide an instance of AuthApi and helper functions
object AppRoute {
    // Default base URL. For Android emulator use 10.0.2.2 to reach localhost on host machine.
    // You can override this by calling `init("https://api.example.com")` early in your app.
    private var baseUrl: String = "http://10.0.2.2:8080"

    private val client = OkHttpClient.Builder().build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val auth: AuthApi by lazy { retrofit.create(AuthApi::class.java) }

    // Allow overriding the base URL (call before accessing `auth` to take effect)
    fun init(newBaseUrl: String) {
        if (newBaseUrl.isNotBlank()) baseUrl = newBaseUrl
    }
}

