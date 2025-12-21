package com.example.fashionapp.data

import com.example.fashionapp.model.*
import retrofit2.http.*

/**
 * Product API interface matching backend endpoints
 */
interface ProductApi {

    /**
     * Create a new product
     * POST /api/products
     */
    @POST("/api/products")
    suspend fun createProduct(@Body request: ProductCreateRequest): ProductApiResponse

    /**
     * Get all products
     * GET /api/products
     */
    @GET("/api/products")
    suspend fun getAllProducts(): List<Product>

    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GET("/api/products/{id}")
    suspend fun getProductById(@Path("id") id: String): Product

    /**
     * Update product
     * PUT /api/products/{id}
     */
    @PUT("/api/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: String,
        @Body request: ProductUpdateRequest
    ): ProductApiResponse

    /**
     * Delete product
     * DELETE /api/products/{id}
     */
    @DELETE("/api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): DeleteResponse

    /**
     * Search products by keyword
     * GET /api/products/search?keyword={keyword}
     */
    @GET("/api/products/search")
    suspend fun searchProducts(
        @Query("keyword") keyword: String? = null
    ): List<Product>

    /**
     * Filter products by gender
     * GET /api/products/filter/gender?value={gender}
     */
    @GET("/api/products/filter/gender")
    suspend fun filterByGender(
        @Query("value") gender: String? = null
    ): List<Product>

    /**
     * Filter products by price range
     * GET /api/products/filter/price?min={minPrice}&max={maxPrice}
     */
    @GET("/api/products/filter/price")
    suspend fun filterByPriceRange(
        @Query("min") minPrice: Double? = null,
        @Query("max") maxPrice: Double? = null
    ): List<Product>
}

