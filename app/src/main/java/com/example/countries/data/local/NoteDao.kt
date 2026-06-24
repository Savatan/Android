package com.example.countries.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM country_notes ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<CountryNoteEntity>>

    @Query("SELECT * FROM country_notes WHERE countryCode = :code LIMIT 1")
    fun observeByCode(code: String): Flow<CountryNoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(note: CountryNoteEntity)

    @Query("DELETE FROM country_notes WHERE countryCode = :code")
    suspend fun deleteByCode(code: String)
}
