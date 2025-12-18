package com.example.marketplaceapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "market_items")
data class MarketItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double,
    val contactPhone: String,
    val imageUri: String? = null,
    val category: String = "General",
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable