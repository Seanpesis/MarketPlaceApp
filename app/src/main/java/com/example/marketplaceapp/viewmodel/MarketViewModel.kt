package com.example.marketplaceapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.marketplaceapp.data.CartItem
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class MarketViewModel @Inject constructor(
    private val repository: MarketRepository,
    application: Application
) : AndroidViewModel(application) {

    private val _allItems: LiveData<List<MarketItem>> = repository.getAllItems()
    private val _currentLocation = MutableLiveData<Location>()
    private val _filterCategory = MutableLiveData("All")

    val currentLocation: LiveData<Location> get() = _currentLocation
    val cartItems: LiveData<List<CartItem>> = CartManager.cartItems

    val finalItemList = MediatorLiveData<List<MarketItem>>()

    init {
        finalItemList.addSource(_allItems) { items -> combineFilterAndSort(items, _currentLocation.value, _filterCategory.value) }
        finalItemList.addSource(_currentLocation) { location -> combineFilterAndSort(_allItems.value, location, _filterCategory.value) }
        finalItemList.addSource(_filterCategory) { category -> combineFilterAndSort(_allItems.value, _currentLocation.value, category) }
    }

    private fun combineFilterAndSort(items: List<MarketItem>?, location: Location?, category: String?) {
        viewModelScope.launch(Dispatchers.Default) {
            val currentItems = items ?: return@launch

            val filteredItems = if (category == null || category == "All") {
                currentItems
            } else {
                currentItems.filter { it.category.equals(category, ignoreCase = true) }
            }

            val sortedItems = if (location == null) {
                filteredItems
            } else {
                filteredItems.sortedBy { item ->
                    item.latitude?.let { lat ->
                        item.longitude?.let { lon ->
                            val itemLocation = Location("").apply {
                                latitude = lat
                                longitude = lon
                            }
                            return@sortedBy location.distanceTo(itemLocation)
                        }
                    }
                    Float.MAX_VALUE
                }
            }
            withContext(Dispatchers.Main) {
                finalItemList.value = sortedItems
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
            itemLiveData.postValue(repository.getItem(itemId))
        }
        return itemLiveData
    }

    suspend fun insert(item: MarketItem): Boolean {
        return repository.insertItem(item)
    }

    suspend fun update(item: MarketItem): Boolean {
        return repository.updateItem(item)
    }

    suspend fun delete(itemId: String): Boolean {
        return repository.deleteItem(itemId)
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
}