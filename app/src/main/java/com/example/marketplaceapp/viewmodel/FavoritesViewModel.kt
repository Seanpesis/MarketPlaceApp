package com.example.marketplaceapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.marketplaceapp.data.FavoriteItem
import com.example.marketplaceapp.data.FavoritesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FavoritesRepository,
    application: Application
) : AndroidViewModel(application) {

    val allFavorites: LiveData<List<FavoriteItem>> = repository.getAllFavorites()

    fun addFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            repository.addFavorite(item)
        }
    }

    fun removeFavorite(item: FavoriteItem) {
        viewModelScope.launch {
            repository.removeFavorite(item)
        }
    }

    suspend fun isFavorite(itemId: String): Boolean {
        return repository.isFavorite(itemId)
    }
}


