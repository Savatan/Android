package com.example.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouriteCountryDao {

    @Query("SELECT * FROM favourite_countries ORDER BY commonName ASC")
    fun observeAll(): Flow<List<FavouriteCountryEntity>>

    @Query("SELECT code FROM favourite_countries")
    fun observeCodes(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavouriteCountryEntity)

    @Query("DELETE FROM favourite_countries WHERE code = :code")
    suspend fun deleteByCode(code: String)
}
