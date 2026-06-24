package com.example.countries.domain.repository

import com.example.countries.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun observeNotes(): Flow<List<Note>>

    fun observeNote(code: String): Flow<Note?>

    suspend fun saveNote(code: String, text: String)

    suspend fun deleteNote(code: String)
}
