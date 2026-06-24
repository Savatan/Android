package com.example.countries.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cached_countries")
data class CachedCountryEntity(
    @PrimaryKey val code: String,
    val commonName: String,
    val officialName: String,
    val flagUrl: String,
    val region: String,
    val subregion: String,
    val capital: String,
    val population: Long,
    val area: Double,
    val languagesCsv: String,
    val currenciesCsv: String,
    val timezonesCsv: String,
    val summaryUpdatedAt: Long,
    val detailUpdatedAt: Long
)

@Entity(tableName = "collections")
data class CollectionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long
)

@Entity(
    tableName = "collection_items",
    primaryKeys = ["collectionId", "countryCode"],
    indices = [Index("countryCode")]
)
data class CollectionItemEntity(
    val collectionId: Long,
    val countryCode: String,
    val addedAt: Long
)

@Entity(tableName = "country_notes")
data class CountryNoteEntity(
    @PrimaryKey val countryCode: String,
    val text: String,
    val updatedAt: Long
)

@Entity(tableName = "recent_views")
data class RecentViewEntity(
    @PrimaryKey val countryCode: String,
    val viewedAt: Long
)
