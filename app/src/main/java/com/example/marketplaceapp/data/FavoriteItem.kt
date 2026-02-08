package com.example.marketplaceapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity(tableName = "favorite_items_table")
data class FavoriteItem(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var imageUrl: String? = null
)
