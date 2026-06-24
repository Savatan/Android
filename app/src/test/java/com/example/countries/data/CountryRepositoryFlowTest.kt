package com.example.countries.data

import app.cash.turbine.test
import com.example.countries.data.repository.CountryRepositoryImpl
import com.example.countries.fake.FakeCountryApi
import com.example.countries.fake.FakeFavouriteDao
import com.example.countries.util.summary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class CountryRepositoryFlowTest {

    private fun repository(dao: FakeFavouriteDao) =
        CountryRepositoryImpl(api = FakeCountryApi(), dao = dao)

    @Test
    fun `favourite codes emit the full sequence as items are added and removed`() = runTest {
        val dao = FakeFavouriteDao()
        val repository = repository(dao)

        repository.observeFavouriteCodes().test {
            assertEquals(emptySet<String>(), awaitItem())

            repository.addFavourite(summary("FRA"))
            assertEquals(setOf("FRA"), awaitItem())

            repository.addFavourite(summary("ITA"))
            assertEquals(setOf("FRA", "ITA"), awaitItem())

            repository.removeFavourite("FRA")
            assertEquals(setOf("ITA"), awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `a new subscriber immediately receives the current favourites`() = runTest {
        val dao = FakeFavouriteDao()
        val repository = repository(dao)

        repository.addFavourite(summary("ESP", "Spain"))

        repository.observeFavourites().test {
            val first = awaitItem()
            assertEquals(1, first.size)
            assertEquals("ESP", first.first().code)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `re-adding an identical favourite does not produce an extra emission`() = runTest {
        val dao = FakeFavouriteDao()
        val repository = repository(dao)

        repository.observeFavourites().test {
            assertEquals(emptyList<Any>(), awaitItem())

            repository.addFavourite(summary("FRA"))
            assertEquals(1, awaitItem().size)

            repository.addFavourite(summary("FRA"))
            expectNoEvents()

            cancelAndIgnoreRemainingEvents()
        }
    }
}
