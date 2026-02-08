package com.example.marketplaceapp.data

import androidx.lifecycle.LiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepository @Inject constructor(
    private val favoriteDao: FavoriteDao
) {
    fun getAllFavorites(): LiveData<List<FavoriteItem>> {
        return favoriteDao.getAllFavorites()
    }

    suspend fun addFavorite(item: FavoriteItem) {
        favoriteDao.insertFavorite(item)
    }

    suspend fun removeFavorite(item: FavoriteItem) {
        favoriteDao.deleteFavorite(item)
    }

    suspend fun isFavorite(itemId: String): Boolean {
        return favoriteDao.isFavorite(itemId)
    }
}


