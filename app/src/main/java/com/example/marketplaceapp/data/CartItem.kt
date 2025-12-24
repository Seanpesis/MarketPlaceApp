package com.example.marketplaceapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val item: MarketItem,
    var quantity: Int
) : Parcelable