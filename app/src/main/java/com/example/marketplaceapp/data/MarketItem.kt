package com.example.marketplaceapp.data

import android.os.Parcelable
import com.google.firebase.firestore.DocumentId
import kotlinx.parcelize.Parcelize

@Parcelize
data class MarketItem(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val contactPhone: String = "",
    val imageUri: String? = null,
    val category: String = "General",
    val latitude: Double? = null,
    val longitude: Double? = null
) : Parcelable
