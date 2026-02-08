package com.example.marketplaceapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_items_table")
data class FavoriteItem(
    @PrimaryKey val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String?
)


