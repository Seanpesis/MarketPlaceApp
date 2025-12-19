package com.example.marketplaceapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MarketItem::class], version = 3, exportSchema = false) // <--- INCREMENTED VERSION NUMBER
abstract class AppDatabase : RoomDatabase() {

    abstract fun marketDao(): MarketDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "market_database"
                )
                // Strategy for migration: if the schema changes, recreate the database.
                // This is simple for development but data will be lost.
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries() // As per assignment requirements
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}