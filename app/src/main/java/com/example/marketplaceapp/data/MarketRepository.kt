package com.example.marketplaceapp.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val itemsCollection = firestore.collection("items")

    suspend fun getAllItems(): List<MarketItem> {
        return try {
            val snapshot = itemsCollection.get().await()
            snapshot.toObjects(MarketItem::class.java)
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error getting items.", e)
            emptyList()
        }
    }

    suspend fun insertItem(item: MarketItem) {
        try {
            itemsCollection.add(item).await()
            Log.d("MarketRepository", "Item added successfully")
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error adding item", e)
        }
    }

    suspend fun updateItem(item: MarketItem) {
        if (item.id.isNotEmpty()) {
            try {
                itemsCollection.document(item.id).set(item).await()
                Log.d("MarketRepository", "Item updated successfully")
            } catch (e: Exception) {
                Log.w("MarketRepository", "Error updating item", e)
            }
        }
    }

    suspend fun getItem(itemId: String): MarketItem? {
        return try {
            val snapshot = itemsCollection.document(itemId).get().await()
            snapshot.toObject(MarketItem::class.java)
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error getting single item.", e)
            null
        }
    }
}