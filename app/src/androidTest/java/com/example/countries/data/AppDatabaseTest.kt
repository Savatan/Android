package com.example.countries.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.countries.data.local.AppDatabase
import com.example.countries.data.local.CachedCountryEntity
import com.example.countries.data.local.CollectionEntity
import com.example.countries.data.local.CollectionItemEntity
import com.example.countries.data.local.CountryNoteEntity
import com.example.countries.data.local.RecentViewEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase

    private fun cached(code: String, name: String, viewedSummaryAt: Long = 1L) =
        CachedCountryEntity(
            code = code, commonName = name, officialName = name, flagUrl = "",
            region = "Europe", subregion = "", capital = "", population = 0L, area = 0.0,
            languagesCsv = "", currenciesCsv = "", timezonesCsv = "",
            summaryUpdatedAt = viewedSummaryAt, detailUpdatedAt = 0L
        )

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun collectionItemsRelationReturnsJoinedCountries() = runBlocking {
        db.countryCacheDao().upsertAll(listOf(cached("FRA", "France"), cached("ITA", "Italy")))
        val id = db.collectionDao().insertCollection(CollectionEntity(name = "Wishlist", createdAt = 1L))
        db.collectionDao().insertItem(CollectionItemEntity(id, "FRA", 2L))

        val countries = db.collectionDao().observeItems(id).first()
        assertEquals(1, countries.size)
        assertEquals("France", countries.first().commonName)
    }

    @Test
    fun recentViewsAreJoinedAndOrderedByTimeDescending() = runBlocking {
        db.countryCacheDao().upsertAll(listOf(cached("FRA", "France"), cached("ITA", "Italy")))
        db.recentViewDao().upsert(RecentViewEntity("FRA", 10L))
        db.recentViewDao().upsert(RecentViewEntity("ITA", 20L))

        val recent = db.recentViewDao().observeRecentCountries(10).first()
        assertEquals("Italy", recent.first().commonName)
    }

    @Test
    fun noteUpsertReplacesExistingNoteForSameCountry() = runBlocking {
        db.noteDao().upsert(CountryNoteEntity("FRA", "first", 1L))
        db.noteDao().upsert(CountryNoteEntity("FRA", "second", 2L))

        val note = db.noteDao().observeByCode("FRA").first()
        assertEquals("second", note?.text)
    }
}
