package com.example.fashionapp

import com.example.fashionapp.data.ProductApi
import com.example.fashionapp.data.CartApi
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

// Data classes for requests
data class UserRegistrationRequest(
    val username: String,
    val email: String,
    val password: String,
    @SerializedName("firstName")
    val first_name: String? = null,
    @SerializedName("lastName")
    val last_name: String? = null,
    @SerializedName("phoneNumber")
    val phone_number: String? = null,
    @SerializedName("userAddress")
    val user_address: String? = null
)

data class UserLoginRequest(
    val username: String,  // Can be email or phone number
    val password: String
)

// Minimal Google OAuth2 user info expected by backend
// NOTE: Backend expects the field to be named "accessToken" even though it's an ID Token
data class GoogleOAuth2UserInfo(
    val accessToken: String, // This is actually the Google ID Token, but backend expects this field name
    val email: String,
    val name: String? = null,
    val picture: String? = null,
    val id: String? = null // Google user ID
)

// User DTO matching backend response
data class UserDto(
    val id: String? = null, // MongoDB ObjectId is returned as String, not Long
    val username: String? = null,
    val email: String? = null,
    @SerializedName("phone_number")
    val phoneNumber: String? = null,
    @SerializedName("user_address")
    val userAddress: String? = null,
    val firstName: String? = null,
    val lastName: String? = null
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

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request()
            android.util.Log.d("AppRoute", "Making request to: ${request.url}")
            android.util.Log.d("AppRoute", "Method: ${request.method}")

            val response = chain.proceed(request)
            android.util.Log.d("AppRoute", "Response code: ${response.code}")
            android.util.Log.d("AppRoute", "Response message: ${response.message}")

            // Log response body for debugging
            val responseBody = response.body
            if (responseBody != null) {
                val bodyString = responseBody.string()
                android.util.Log.d("AppRoute", "Response body: $bodyString")

                // Create new response with the consumed body
                val newResponseBody = ResponseBody.create(
                    responseBody.contentType(),
                    bodyString
                )
                return@addInterceptor response.newBuilder()
                    .body(newResponseBody)
                    .build()
            }

            response
        }
        .build()

    private val gson = GsonBuilder()
        .setLenient() // More lenient JSON parsing
        .serializeNulls() // Handle null values properly
        .create()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    val auth: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val product: ProductApi by lazy { retrofit.create(ProductApi::class.java) }
    val cart: CartApi by lazy { retrofit.create(CartApi::class.java) }

    // Allow overriding the base URL (call before accessing `auth` to take effect)
    fun init(newBaseUrl: String) {
        if (newBaseUrl.isNotBlank()) baseUrl = newBaseUrl
    }
}

