package com.example.marketplaceapp.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton



@Singleton
class MarketRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val apiService: FakeStoreApiService
) {

    private val itemsCollection = firestore.collection("items")
    private val favoritesCollection = firestore.collection("favorites")


    suspend fun fetchApiProducts(): List<ApiProduct> {
        return apiService.getAllProducts()
    }

    suspend fun getApiProduct(id: Int): ApiProduct? {
        return try {
            apiService.getProductById(id)
        } catch (e: Exception) {
            Log.e("MarketRepository", "Error fetching single product from API", e)
            null
        }
    }

    fun getAllItems(): LiveData<List<MarketItem>> {
        val liveData = MutableLiveData<List<MarketItem>>()
        itemsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("MarketRepository", "Listen failed.", error)
                liveData.value = emptyList()
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { doc ->
                    val item = doc.toObject(MarketItem::class.java)
                    item?.id = doc.id // Manually set the ID
                    item
                }
                Log.d("MarketRepository", "Loaded ${items.size} items from Firestore.")
                liveData.value = items
            } else {
                liveData.value = emptyList()
            }
        }
        return liveData
    }

    suspend fun postNewProduct(product: ApiProduct): ApiProduct? {
        return try {
            apiService.addProduct(product)
        } catch (e: Exception) {
            null
        }
    }




    suspend fun insertItem(item: MarketItem): Boolean {
        return try {
            val docRef = itemsCollection.document()
            item.id = docRef.id // Assign ID before saving
            docRef.set(item).await()
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
            val item = snapshot.toObject(MarketItem::class.java)
            item?.id = snapshot.id // Manually set the ID
            item
        } catch (e: Exception) {
            Log.w("MarketRepository", "Error getting single item.", e)
            null
        }
    }

    fun getFavoriteItems(): LiveData<List<FavoriteItem>> {
        val liveData = MutableLiveData<List<FavoriteItem>>()
        favoritesCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                liveData.value = emptyList()
                return@addSnapshotListener
            }
            snapshot?.let {
                val favoriteItems = it.documents.mapNotNull { doc ->
                    val item = doc.toObject(FavoriteItem::class.java)
                    item?.id = doc.id
                    item
                }
                liveData.value = favoriteItems
            }
        }
        return liveData
    }

    fun getFavoriteItemIds(): LiveData<Set<String>> {
        return getFavoriteItems().map { it.map { item -> item.id }.toSet() }
    }

    suspend fun addFavorite(favoriteItem: FavoriteItem) {
        try {
            favoritesCollection.document(favoriteItem.id).set(favoriteItem).await()
        } catch (e: Exception) {
             Log.w("MarketRepository", "Error adding favorite", e)
        }
    }

    suspend fun removeFavorite(itemId: String) {
         try {
            favoritesCollection.document(itemId).delete().await()
        } catch (e: Exception) {
             Log.w("MarketRepository", "Error removing favorite", e)
        }
    }




}