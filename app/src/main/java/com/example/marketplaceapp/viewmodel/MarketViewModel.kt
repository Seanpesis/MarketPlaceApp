package com.example.marketplaceapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.marketplaceapp.data.CartItem
import com.example.marketplaceapp.data.CartManager
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MarketRepository()
    private val _allItems = repository.getAllItems()
    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> get() = _currentLocation // Added this back

        // private val _cartItems = MutableLiveData<List<CartItem>>(emptyList())
        val cartItems: LiveData<MutableList<CartItem>> get() = CartManager.cartItems


    //val cartItems: LiveData<List<CartItem>> get() = _cartItems

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

    fun addToCart(marketItem: MarketItem) {
        CartManager.addToCart(marketItem)
//        val currentList = _cartItems.value ?: emptyList()
//        val newList = currentList.toMutableList()
//        val existingItem = newList.find { it.item.id == marketItem.id }
//
//        if (existingItem != null) {
//            existingItem.quantity++
//        } else {
//            newList.add(CartItem(item = marketItem, quantity = 1))
//        }
//        _cartItems.value = newList
    }

    fun removeFromCart(cartItem: CartItem) {
//        val currentList = _cartItems.value ?: emptyList()
//        val newList = currentList.toMutableList()
//        newList.remove(cartItem)
//        _cartItems.value = newList
        CartManager.removeFromCart(cartItem)
    }

    fun clearCart() {
//        _cartItems.value = emptyList()
        CartManager.clearCart()
    }
}