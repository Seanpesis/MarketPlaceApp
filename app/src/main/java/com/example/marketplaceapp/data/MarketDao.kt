package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MarketDao {

    @Query("SELECT * FROM market_items ORDER BY id DESC")
    fun getAllItems(): LiveData<List<MarketItem>>

    @Query("SELECT * FROM market_items WHERE id = :itemId")
    fun getItemById(itemId: Long): LiveData<MarketItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: MarketItem)

    @Update
    suspend fun updateItem(item: MarketItem)

    @Delete
    suspend fun deleteItem(item: MarketItem)
}