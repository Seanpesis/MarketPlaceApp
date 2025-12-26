package com.example.marketplaceapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MarketRepository()
    private val _allItems = repository.getAllItems()
    private val _currentLocation = MutableLiveData<Location>()
    private val _cartItems = MutableLiveData<MutableList<MarketItem>>(mutableListOf())

    val currentLocation: LiveData<Location> get() = _currentLocation
    val cartItems: LiveData<MutableList<MarketItem>> get() = _cartItems

    val finalItemList = MediatorLiveData<List<MarketItem>>()

    init {
        finalItemList.addSource(_allItems) { items -> combineAndSort(items, _currentLocation.value, null) }
        finalItemList.addSource(_currentLocation) { location -> combineAndSort(_allItems.value, location, null) }
    }

    private fun combineAndSort(items: List<MarketItem>?, location: Location?, category: String?) {
        if (items == null) {
            finalItemList.value = emptyList()
            return
        }

        val filteredItems = if (category == null || category == "All") {
            items
        } else {
            items.filter { it.category.equals(category, ignoreCase = true) }
        }

        val sortedItems = if (location == null) {
            filteredItems
        } else {
            filteredItems.sortedBy { item ->
                if (item.latitude != null && item.longitude != null) {
                    val itemLocation = Location("").apply {
                        latitude = item.latitude!!
                        longitude = item.longitude!!
                    }
                    location.distanceTo(itemLocation)
                } else {
                    Float.MAX_VALUE
                }
            }
        }
        finalItemList.value = sortedItems
    }

    fun setFilter(category: String?) {
        combineAndSort(_allItems.value, _currentLocation.value, category)
    }

    fun setCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    fun getItem(id: String): LiveData<MarketItem> {
        return repository.getItem(id)
    }

    fun insert(item: MarketItem) {
        repository.insertItem(item)
    }

    fun update(item: MarketItem) {
        repository.updateItem(item)
    }

    fun delete(item: MarketItem) {
        repository.deleteItem(item)
    }

    fun addToCart(item: MarketItem) {
        val list = _cartItems.value ?: mutableListOf()
        list.add(item)
        _cartItems.value = list
    }

    fun removeFromCart(item: MarketItem) {
        val list = _cartItems.value ?: mutableListOf()
        list.remove(item)
        _cartItems.value = list
    }

    fun clearCart() {
        _cartItems.value = mutableListOf()
    }
}