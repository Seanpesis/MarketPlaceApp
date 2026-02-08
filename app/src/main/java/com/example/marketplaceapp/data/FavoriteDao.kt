package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(item: FavoriteItem)

    @Delete
    suspend fun deleteFavorite(item: FavoriteItem)

    @Query("SELECT * FROM favorite_items_table ORDER BY name ASC")
    fun getAllFavorites(): LiveData<List<FavoriteItem>>

    @Query("SELECT EXISTS(SELECT * FROM favorite_items_table WHERE id = :itemId)")
    suspend fun isFavorite(itemId: String): Boolean
}


