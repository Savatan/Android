package com.example.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CountryCacheDao {

    @Query("SELECT * FROM cached_countries ORDER BY commonName ASC")
    fun observeAll(): Flow<List<CachedCountryEntity>>

    @Query("SELECT * FROM cached_countries WHERE code = :code LIMIT 1")
    fun observeByCode(code: String): Flow<CachedCountryEntity?>

    @Query("SELECT * FROM cached_countries WHERE code = :code LIMIT 1")
    suspend fun getByCode(code: String): CachedCountryEntity?

    @Query("SELECT MAX(summaryUpdatedAt) FROM cached_countries")
    fun observeLastSync(): Flow<Long?>

    @Query("SELECT MAX(summaryUpdatedAt) FROM cached_countries")
    suspend fun lastSync(): Long?

    @Query("SELECT COUNT(*) FROM cached_countries")
    suspend fun count(): Int

    @Query("SELECT * FROM cached_countries")
    suspend fun getAllOnce(): List<CachedCountryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(items: List<CachedCountryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: CachedCountryEntity)
}
