package com.example.marketplaceapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteProduct::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
}