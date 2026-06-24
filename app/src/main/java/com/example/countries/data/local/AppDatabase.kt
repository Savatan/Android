package com.example.countries.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CachedCountryEntity::class,
        CollectionEntity::class,
        CollectionItemEntity::class,
        CountryNoteEntity::class,
        RecentViewEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countryCacheDao(): CountryCacheDao
    abstract fun collectionDao(): CollectionDao
    abstract fun noteDao(): NoteDao
    abstract fun recentViewDao(): RecentViewDao

    companion object {
        const val NAME = "countries.db"
    }
}
