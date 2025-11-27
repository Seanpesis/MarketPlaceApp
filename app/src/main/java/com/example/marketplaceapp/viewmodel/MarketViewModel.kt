package com.example.marketplaceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.marketplaceapp.data.AppDatabase
import com.example.marketplaceapp.data.MarketItem
import com.example.marketplaceapp.data.MarketRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MarketViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MarketRepository
    val allItems: LiveData<List<MarketItem>>

    init {
        val dao = AppDatabase.getDatabase(application).marketDao()
        repository = MarketRepository(dao)
        allItems = repository.allItems
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