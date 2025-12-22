package com.example.fashionapp.data

import retrofit2.http.*

/**
 * User API interface matching backend endpoints
 */
interface UserApi {

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GET("/api/users/{id}")
    suspend fun getUserById(@Path("id") id: String): UserResponse

    /**
     * Update user name
     * PUT /api/users/{id}/name
     */
    @PUT("/api/users/{id}/name")
    suspend fun updateUserName(
        @Path("id") id: String,
        @Body request: UpdateNameRequest
    ): UpdateNameResponse

    /**
     * Update user email
     * PUT /api/users/{id}/email
     */
    @PUT("/api/users/{id}/email")
    suspend fun updateUserEmail(
        @Path("id") id: String,
        @Body request: UpdateEmailRequest
    ): UpdateEmailResponse

    /**
     * Update user phone number
     * PUT /api/users/{id}/phone
     */
    @PUT("/api/users/{id}/phone")
    suspend fun updateUserPhone(
        @Path("id") id: String,
        @Body request: UpdatePhoneRequest
    ): UpdatePhoneResponse

    /**
     * Update user password
     * PUT /api/users/{id}/password
     */
    @PUT("/api/users/{id}/password")
    suspend fun updateUserPassword(
        @Path("id") id: String,
        @Body request: UpdatePasswordRequest
    ): UpdatePasswordResponse

    /**
     * Get user address
     * GET /api/users/{id}/address
     */
    @GET("/api/users/{id}/address")
    suspend fun getUserAddress(@Path("id") id: String): AddressResponse

    /**
     * Update user address
     * PUT /api/users/{id}/address
     */
    @PUT("/api/users/{id}/address")
    suspend fun updateUserAddress(
        @Path("id") id: String,
        @Body request: UpdateAddressRequest
    ): UpdateAddressResponse
}

// Request DTOs
data class UpdateNameRequest(
    val firstName: String,
    val lastName: String
)

data class UpdateEmailRequest(
    val email: String
)

data class UpdatePhoneRequest(
    val phoneNumber: String
)

data class UpdatePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

data class UpdateAddressRequest(
    val user_address: String
)

// Response DTOs
data class UserResponse(
    val id: String,
    val username: String?,
    val email: String?,
    val phoneNumber: String?,
    val userAddress: String?,
    val firstName: String?,
    val lastName: String?
)

data class UpdateNameResponse(
    val message: String,
    val firstName: String,
    val lastName: String
)

data class UpdateEmailResponse(
    val message: String,
    val email: String
)

data class UpdatePhoneResponse(
    val message: String,
    val phoneNumber: String
)

data class UpdatePasswordResponse(
    val message: String
)

data class AddressResponse(
    val user_address: String
)

data class UpdateAddressResponse(
    val message: String,
    val user_address: String
)

data class ErrorResponse(
    val error: String
)

