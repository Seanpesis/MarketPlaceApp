package com.example.marketplaceapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Parcelize
@IgnoreExtraProperties
@Entity(tableName = "market_items_table")
data class MarketItem(
    @PrimaryKey
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var contactPhone: String = "",
    var imageUri: String? = null,
    var latitude: Double? = null,
    var longitude: Double? = null
) : Parcelable
