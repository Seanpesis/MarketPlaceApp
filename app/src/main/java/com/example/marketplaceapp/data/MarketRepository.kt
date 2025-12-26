package com.example.marketplaceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObjects

class MarketRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val itemsCollection = firestore.collection("items")

    fun getAllItems(): LiveData<List<MarketItem>> {
        val liveData = MutableLiveData<List<MarketItem>>()
        itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("MarketRepository", "Listen failed.", error)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.toObjects<MarketItem>()
                liveData.value = items
            } else {
                liveData.value = emptyList()
            }
        }
        return liveData
    }

    fun insertItem(item: MarketItem) {
        itemsCollection.add(item)
            .addOnSuccessListener { Log.d("MarketRepository", "Item added successfully") }
            .addOnFailureListener { e -> Log.w("MarketRepository", "Error adding item", e) }
    }

    fun updateItem(item: MarketItem) {
        if (item.id.isNotEmpty()) {
            itemsCollection.document(item.id).set(item)
                .addOnSuccessListener { Log.d("MarketRepository", "Item updated successfully") }
                .addOnFailureListener { e -> Log.w("MarketRepository", "Error updating item", e) }
        }
    }

    fun deleteItem(item: MarketItem) {
        if (item.id.isNotEmpty()) {
            itemsCollection.document(item.id).delete()
                .addOnSuccessListener { Log.d("MarketRepository", "Item deleted successfully") }
                .addOnFailureListener { e -> Log.w("MarketRepository", "Error deleting item", e) }
        }
    }
    
    fun getItem(itemId: String): LiveData<MarketItem> {
        val itemLiveData = MutableLiveData<MarketItem>()
        itemsCollection.document(itemId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("MarketRepository", "Listen failed for single item.", error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                itemLiveData.value = snapshot.toObject(MarketItem::class.java)
            }
        }
        return itemLiveData
    }
}