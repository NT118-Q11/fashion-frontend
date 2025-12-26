package com.example.fashionapp.data

import com.example.fashionapp.model.*
import retrofit2.http.*

/**
 * Product Information API interface matching backend endpoints
 * Endpoint: /api/product-information
 */
interface ProductInformationApi {

    /**
     * Create product information
     * POST /api/product-information
     */
    @POST("/api/product-information")
    suspend fun createProductInformation(
        @Body request: ProductInformationCreateRequest
    ): ProductInformationApiResponse

    /**
     * Get all product information
     * GET /api/product-information
     */
    @GET("/api/product-information")
    suspend fun getAllProductInformation(): List<ProductInformation>

    /**
     * Get product information by ID
     * GET /api/product-information/{id}
     */
    @GET("/api/product-information/{id}")
    suspend fun getProductInformationById(@Path("id") id: String): ProductInformation

    /**
     * Get product information by product ID
     * GET /api/product-information/product/{productId}
     */
    @GET("/api/product-information/product/{productId}")
    suspend fun getProductInformationByProductId(@Path("productId") productId: String): ProductInformation

    /**
     * Update product information by ID
     * PUT /api/product-information/{id}
     */
    @PUT("/api/product-information/{id}")
    suspend fun updateProductInformation(
        @Path("id") id: String,
        @Body request: ProductInformationUpdateRequest
    ): ProductInformationApiResponse

    /**
     * Update product information by product ID
     * PUT /api/product-information/product/{productId}
     */
    @PUT("/api/product-information/product/{productId}")
    suspend fun updateProductInformationByProductId(
        @Path("productId") productId: String,
        @Body request: ProductInformationUpdateRequest
    ): ProductInformationApiResponse

    /**
     * Delete product information by ID
     * DELETE /api/product-information/{id}
     */
    @DELETE("/api/product-information/{id}")
    suspend fun deleteProductInformation(@Path("id") id: String): ProductInformationDeleteResponse

    /**
     * Delete product information by product ID
     * DELETE /api/product-information/product/{productId}
     */
    @DELETE("/api/product-information/product/{productId}")
    suspend fun deleteProductInformationByProductId(@Path("productId") productId: String): ProductInformationDeleteResponse

    /**
     * Filter product information by brand
     * GET /api/product-information/filter/brand?value={brand}
     */
    @GET("/api/product-information/filter/brand")
    suspend fun filterByBrand(@Query("value") brand: String? = null): List<ProductInformation>

    /**
     * Filter product information by category
     * GET /api/product-information/filter/category?value={category}
     */
    @GET("/api/product-information/filter/category")
    suspend fun filterByCategory(@Query("value") category: String? = null): List<ProductInformation>

    /**
     * Filter product information by gender
     * GET /api/product-information/filter/gender?value={gender}
     */
    @GET("/api/product-information/filter/gender")
    suspend fun filterByGender(@Query("value") gender: String? = null): List<ProductInformation>

    /**
     * Filter product information by origin
     * GET /api/product-information/filter/origin?value={origin}
     */
    @GET("/api/product-information/filter/origin")
    suspend fun filterByOrigin(@Query("value") origin: String? = null): List<ProductInformation>

    /**
     * Filter product information by fit
     * GET /api/product-information/filter/fit?value={fit}
     */
    @GET("/api/product-information/filter/fit")
    suspend fun filterByFit(@Query("value") fit: String? = null): List<ProductInformation>
}

