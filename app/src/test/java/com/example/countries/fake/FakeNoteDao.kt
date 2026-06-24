package com.example.countries.fake

import com.example.countries.data.local.CountryNoteEntity
import com.example.countries.data.local.NoteDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNoteDao : NoteDao {

    private val state = MutableStateFlow<Map<String, CountryNoteEntity>>(emptyMap())

    override fun observeAll(): Flow<List<CountryNoteEntity>> =
        state.map { it.values.sortedByDescending { n -> n.updatedAt } }

    override fun observeByCode(code: String): Flow<CountryNoteEntity?> =
        state.map { it[code] }

    override suspend fun upsert(note: CountryNoteEntity) {
        state.update { it + (note.countryCode to note) }
    }

    override suspend fun deleteByCode(code: String) {
        state.update { it - code }
    }

    fun current(): Map<String, CountryNoteEntity> = state.value
}
