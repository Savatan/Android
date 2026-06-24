package com.example.countries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavouriteCountryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteCountryDao(): FavouriteCountryDao

    companion object {
        const val NAME = "countries.db"
    }
}
