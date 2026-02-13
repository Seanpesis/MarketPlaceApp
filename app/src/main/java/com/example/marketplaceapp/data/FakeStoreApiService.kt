package com.example.marketplaceapp.data


import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Body

interface FakeStoreApiService {
    @GET("carts")
    suspend fun getCarts(): List<ApiCart>

    @GET("products")
    suspend fun getAllProducts(): List<ApiProduct>

    @POST("products")
    suspend fun addProduct(@Body product: ApiProduct): ApiProduct
}