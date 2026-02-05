package com.example.marketplaceapp.data

import retrofit2.http.GET

interface FakeStoreApiService {
    @GET("carts")
    suspend fun getCarts(): List<ApiCart>
}