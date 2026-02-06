package com.example.marketplaceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MarketRepository @Inject constructor(
    firestore: FirebaseFirestore
) {

    private val itemsCollection = firestore.collection("items")

    fun getAllItems(): LiveData<List<MarketItem>> {
        val liveData = MutableLiveData<List<MarketItem>>()
        itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("MarketRepository", "Listen failed.", error)
                liveData.value = emptyList()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.toObjects(MarketItem::class.java)
                Log.d("MarketRepository", "Loaded ${items.size} items from Firestore.")
                liveData.value = items
            } else {
                liveData.value = emptyList()
            }
        }
        return liveData
    }

    suspend fun insertItem(item: MarketItem): Boolean {
        return try {
            itemsCollection.add(item).await()
            Log.d("MarketRepository", "Item added successfully")
            true
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error adding item", e)
            false
        }
    }

    suspend fun updateItem(item: MarketItem): Boolean {
        if (item.id.isEmpty()) return false
        return try {
            itemsCollection.document(item.id).set(item).await()
            Log.d("MarketRepository", "Item updated successfully")
            true
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error updating item", e)
            false
        }
    }

    suspend fun deleteItem(itemId: String): Boolean {
        if (itemId.isEmpty()) return false
        return try {
            itemsCollection.document(itemId).delete().await()
            Log.d("MarketRepository", "Item deleted successfully")
            true
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error deleting item", e)
            false
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