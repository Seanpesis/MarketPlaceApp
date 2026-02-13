package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(product: FavoriteProduct)

    @Delete
    suspend fun deleteFavorite(product: FavoriteProduct)

    @Query("SELECT * FROM favorites_table ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteProduct>>

    @Query("SELECT EXISTS(SELECT * FROM favorites_table WHERE id = :productId)")
    suspend fun isFavorite(productId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: MarketItem)

    @Query("SELECT * FROM market_items_table")
    fun getAllProducts(): LiveData<List<MarketItem>>
}