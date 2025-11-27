package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData

class MarketRepository(private val marketDao: MarketDao) {

    val allItems: LiveData<List<MarketItem>> = marketDao.getAllItems()

    fun getItem(id: Long): LiveData<MarketItem> {
        return marketDao.getItemById(id)
    }

    suspend fun insert(item: MarketItem) {
        marketDao.insertItem(item)
    }

    suspend fun update(item: MarketItem) {
        marketDao.updateItem(item)
    }

    suspend fun delete(item: MarketItem) {
        marketDao.deleteItem(item)
    }
}