package com.example.countries.data

import com.example.countries.data.repository.NotesRepositoryImpl
import com.example.countries.fake.FakeNoteDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotesRepositoryImplTest {

    @Test
    fun `saving a note stores trimmed text`() = runTest {
        val repo = NotesRepositoryImpl(FakeNoteDao())

        repo.saveNote("FRA", "  loved Paris  ")

        assertEquals("loved Paris", repo.observeNote("FRA").first()?.text)
    }

    @Test
    fun `saving a blank note removes it`() = runTest {
        val dao = FakeNoteDao()
        val repo = NotesRepositoryImpl(dao)

        repo.saveNote("FRA", "temp")
        repo.saveNote("FRA", "   ")

        assertNull(repo.observeNote("FRA").first())
    }

    @Test
    fun `deleting a note removes it`() = runTest {
        val repo = NotesRepositoryImpl(FakeNoteDao())

        repo.saveNote("FRA", "note")
        repo.deleteNote("FRA")

        assertNull(repo.observeNote("FRA").first())
    }
}
