package com.example.countries.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.countries.data.local.AppDatabase
import com.example.countries.data.repository.CountryRepositoryImpl
import com.example.countries.domain.model.CountrySummary
import com.example.countries.fake.FakeApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FavouritesRoomIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: CountryRepositoryImpl

    private fun summary(code: String, name: String = code) = CountrySummary(
        code = code,
        commonName = name,
        flagUrl = "https://flags/$code.png",
        region = "Europe",
        capital = "Capital-$code",
        population = 1_000
    )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = CountryRepositoryImpl(api = FakeApi(), dao = database.favouriteCountryDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addFavourite_isPersistedAndReadBack() = runTest {
        repository.addFavourite(summary("FRA", "France"))

        val stored = repository.observeFavourites().first()

        assertEquals(1, stored.size)
        assertEquals("FRA", stored.first().code)
        assertEquals("France", stored.first().commonName)
    }

    @Test
    fun addingSameCountryTwice_doesNotCreateDuplicateRow() = runTest {
        repository.addFavourite(summary("ITA", "Italy"))
        repository.addFavourite(summary("ITA", "Italy"))

        val stored = repository.observeFavourites().first()

        assertEquals(1, stored.size)
    }

    @Test
    fun removeFavourite_deletesFromRoom() = runTest {
        repository.addFavourite(summary("ESP"))
        repository.removeFavourite("ESP")

        val stored = repository.observeFavourites().first()

        assertEquals(0, stored.size)
    }
}
