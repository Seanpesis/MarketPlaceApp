package com.example.marketplaceapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.marketplaceapp.data.AppDatabase
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MarketRepository

    private val _allItems: LiveData<List<MarketItem>>
    private val _currentLocation = MutableLiveData<Location>()

    val currentLocation: LiveData<Location> get() = _currentLocation

    // This will hold the sorted list of items
    val sortedItems = MediatorLiveData<List<MarketItem>>()

    init {
        val dao = AppDatabase.getDatabase(application).marketDao()
        repository = MarketRepository(dao)
        _allItems = repository.allItems

        // Observe both the item list and the location for changes
        sortedItems.addSource(_allItems) { items ->
            sortItemsByDistance(items, _currentLocation.value)
        }
        sortedItems.addSource(_currentLocation) { location ->
            sortItemsByDistance(_allItems.value, location)
        }
    }

    private fun sortItemsByDistance(items: List<MarketItem>?, location: Location?) {
        if (items == null || location == null) {
            sortedItems.value = items
            return
        }

        val sorted = items.sortedBy { item ->
            if (item.latitude != null && item.longitude != null) {
                val itemLocation = Location("").apply {
                    latitude = item.latitude
                    longitude = item.longitude
                }
                location.distanceTo(itemLocation)
            } else {
                Float.MAX_VALUE // Put items without location at the end
            }
        }
        sortedItems.value = sorted
    }

    fun setCurrentLocation(location: Location) {
        _currentLocation.value = location
    }

    fun getItem(id: Long): LiveData<MarketItem> {
        return repository.getItem(id)
    }

    fun insert(item: MarketItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(item)
        }
    }

    fun update(item: MarketItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.update(item)
        }
    }

    fun delete(item: MarketItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(item)
        }
    }
}