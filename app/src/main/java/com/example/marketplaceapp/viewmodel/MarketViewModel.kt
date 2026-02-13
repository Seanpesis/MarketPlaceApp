package com.example.marketplaceapp.viewmodel

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.marketplaceapp.data.CartItem
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.data.FavoriteItem
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import com.example.marketplaceapp.data.ApiProduct

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: MarketRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allItems: LiveData<List<MarketItem>> = repository.getAllItems()
    private val _currentLocation = MutableLiveData<Location>()
    private val _filterCategory = MutableLiveData("All")

    private val _externalApiItems = MutableLiveData<List<MarketItem>>()

    val currentLocation: LiveData<Location> get() = _currentLocation
    val cartItems: LiveData<List<CartItem>> = CartManager.cartItems

    val finalItemList = MediatorLiveData<List<MarketItem>>()

    val favoriteItems: LiveData<List<FavoriteItem>> = repository.getFavoriteItems()
    val favoriteItemIds: LiveData<Set<String>> = repository.getFavoriteItemIds()

    init {
        finalItemList.addSource(_allItems) { items -> combineFilterAndSort(items, _currentLocation.value, _filterCategory.value) }
        finalItemList.addSource(_currentLocation) { location -> combineFilterAndSort(_allItems.value, location, _filterCategory.value) }
        finalItemList.addSource(_filterCategory) { category -> combineFilterAndSort(_allItems.value, _currentLocation.value, category) }
        finalItemList.addSource(_externalApiItems) { _ ->
            combineFilterAndSort(_allItems.value, _currentLocation.value, _filterCategory.value)
        }
    }

    private fun combineFilterAndSort(items: List<MarketItem>?, location: Location?, category: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val firebaseItems = items ?: emptyList()
            val apiItems = _externalApiItems.value ?: emptyList()
            val currentItems = firebaseItems + apiItems

            if (currentItems.isEmpty()) {
                withContext(Dispatchers.Main) {
                    finalItemList.value = emptyList()
                }
                return@launch
            }

            val filteredItems = if (category == null || category == "All") {
                currentItems
            } else {
                currentItems.filter { it.category.equals(category, ignoreCase = true) }
            }

            val sortedItems = if (location == null) {
                filteredItems
            } else {
                filteredItems.sortedBy { item ->
                    val lat = item.latitude
                    val lon = item.longitude

                    if (lat != null && lon != null && lat != 0.0) {
                        val itemLocation = Location("").apply {
                            latitude = lat
                            longitude = lon
                        }
                        location.distanceTo(itemLocation)
                    } else {
                        Float.MAX_VALUE
                    }
                }
            }

            withContext(Dispatchers.Main) {
                finalItemList.value = sortedItems
            }
        }
    }

    fun fetchExternalProducts() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val apiProducts = repository.fetchApiProducts()
                val mappedItems = apiProducts.map { apiProd: ApiProduct ->
                    MarketItem(
                        id = "api_${apiProd.id}",
                        title = apiProd.title,
                        price = apiProd.price,
                        category = when(apiProd.category) {
                            "electronics" -> "Technology"
                            "men's clothing", "women's clothing" -> "Clothing"
                            else -> "All"
                        },
                        imageUri = apiProd.image,
                        description = apiProd.description
                    )
                }

                _externalApiItems.postValue(mappedItems)
            } catch (e: Exception) {
                Log.e("API_ERROR", "Failed to fetch products", e)
            }
        }
    }

    fun setFilter(category: String?) {
        _filterCategory.value = category
    }

    fun setCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    fun getItem(itemId: String): LiveData<MarketItem?> {
        val itemLiveData = MutableLiveData<MarketItem?>()
        viewModelScope.launch {
            if (itemId.startsWith("api_")) {
                try {
                    val apiId = itemId.removePrefix("api_").toInt()
                    val apiProduct = repository.getApiProduct(apiId)
                    if (apiProduct != null) {
                        val marketItem = MarketItem(
                            id = "api_${apiProduct.id}",
                            title = apiProduct.title,
                            price = apiProduct.price,
                            category = when(apiProduct.category) {
                                "electronics" -> "Technology"
                                "men's clothing", "women's clothing" -> "Clothing"
                                else -> "All"
                            },
                            imageUri = apiProduct.image,
                            description = apiProduct.description
                        )
                        itemLiveData.postValue(marketItem)
                    } else {
                        itemLiveData.postValue(null)
                    }
                } catch (e: NumberFormatException) {
                    Log.w("MarketViewModel", "Invalid API ID format in getItem", e)
                    itemLiveData.postValue(null) // Invalid ID format
                }
            } else {
                itemLiveData.postValue(repository.getItem(itemId))
            }
        }
        return itemLiveData
    }

    suspend fun delete(itemId: String): Boolean {
        return repository.deleteItem(itemId)
    }

    fun addItem(
        item: MarketItem,
        addToGlobalStore: Boolean,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val localSuccess = repository.insertItem(item)

                if (addToGlobalStore) {
                    val apiProduct = ApiProduct(
                        id = 0,
                        title = item.title,
                        price = item.price,
                        description = item.description,
                        category = item.category,
                        image = item.imageUri ?: ""
                    )

                    val apiResult = repository.postNewProduct(apiProduct)

                    if (apiResult != null) {
                        Log.d("API_POST", "Successfully posted to Global Store: ${apiResult.id}")
                    }
                }

                withContext(Dispatchers.Main) {
                    onComplete(localSuccess)
                }
            } catch (e: Exception) {
                Log.e("ADD_ITEM_ERROR", "Error in addItem", e)
                withContext(Dispatchers.Main) {
                    onComplete(false)
                }
            }
        }
    }

    fun addToCart(marketItem: MarketItem) {
        CartManager.addToCart(marketItem)
    }

    fun removeFromCart(cartItem: CartItem) {
        CartManager.removeFromCart(cartItem)
    }

    fun clearCart() {
        CartManager.clearCart()
    }

    fun toggleFavorite(item: MarketItem) {
        viewModelScope.launch {
            if (item.id.isEmpty()) return@launch

            if (favoriteItemIds.value?.contains(item.id) == true) {
                repository.removeFavorite(item.id)
            } else {
                val favoriteItem = FavoriteItem(
                    id = item.id,
                    name = item.title,
                    price = item.price,
                    imageUrl = item.imageUri
                )
                repository.addFavorite(favoriteItem)
            }
        }
    }

    fun removeFavorite(itemId: String) {
        viewModelScope.launch {
            if (itemId.isEmpty()) return@launch
            repository.removeFavorite(itemId)
        }
    }
}