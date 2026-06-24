package com.example.countries.data.repository

import com.example.countries.data.local.CountryNoteEntity
import com.example.countries.data.local.NoteDao
import com.example.countries.data.mapper.toNote
import com.example.countries.domain.model.Note
import com.example.countries.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotesRepositoryImpl @Inject constructor(
    private val dao: NoteDao
) : NotesRepository {

    override fun observeNotes(): Flow<List<Note>> =
        dao.observeAll().map { list -> list.map { it.toNote() } }

    override fun observeNote(code: String): Flow<Note?> =
        dao.observeByCode(code).map { it?.toNote() }

    override suspend fun saveNote(code: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            dao.deleteByCode(code)
        } else {
            dao.upsert(CountryNoteEntity(code, trimmed, System.currentTimeMillis()))
        }
    }

    override suspend fun deleteNote(code: String) {
        dao.deleteByCode(code)
    }
}
