package com.example.marketplaceapp.data

data class ApiCart(
    val id: Int,
    val userId: Int,
    val products: List<ApiProduct>
)