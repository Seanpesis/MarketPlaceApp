package com.example.marketplaceapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites_table")data class FavoriteProduct(
    @PrimaryKey val id: String,
    val title: String,
    val price: String,
    val imageUrl: String,
    val category: String,
    val timestamp: Long = System.currentTimeMillis()
)