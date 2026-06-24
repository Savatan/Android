package com.example.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentViewDao {

    @Query(
        "SELECT cached_countries.* FROM recent_views " +
            "INNER JOIN cached_countries ON cached_countries.code = recent_views.countryCode " +
            "ORDER BY recent_views.viewedAt DESC LIMIT :limit"
    )
    fun observeRecentCountries(limit: Int): Flow<List<CachedCountryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(view: RecentViewEntity)

    @Query("DELETE FROM recent_views")
    suspend fun clear()

    @Query("SELECT COUNT(*) FROM recent_views")
    suspend fun count(): Int
}
