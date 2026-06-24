package com.example.countries.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_countries")
data class FavouriteCountryEntity(
    @PrimaryKey val code: String,
    val commonName: String,
    val flagUrl: String,
    val region: String,
    val capital: String,
    val population: Long
)
