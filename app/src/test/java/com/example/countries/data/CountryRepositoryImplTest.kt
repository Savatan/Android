package com.example.countries.data

import com.example.countries.data.repository.CountryRepositoryImpl
import com.example.countries.fake.FakeCountryApi
import com.example.countries.fake.FakeCountryCacheDao
import com.example.countries.util.cached
import com.example.countries.util.dto
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class CountryRepositoryImplTest {

    @Test
    fun `isStale is true when cache older than ttl and false when fresh`() = runTest {
        val dao = FakeCountryCacheDao(listOf(cached("FRA", summaryUpdatedAt = 1_000L)))
        val repo = CountryRepositoryImpl(FakeCountryApi(), dao)

        assertTrue(repo.isStale(ttlMillis = 100L, now = 2_000L))
        assertFalse(repo.isStale(ttlMillis = 100L, now = 1_050L))
    }

    @Test
    fun `refresh stores fetched countries into the cache`() = runTest {
        val dao = FakeCountryCacheDao()
        val api = FakeCountryApi().apply { allResult = { listOf(dto("FRA"), dto("ITA")) } }
        val repo = CountryRepositoryImpl(api, dao)

        val result = repo.refresh()

        assertTrue(result.isSuccess)
        assertEquals(2, repo.observeCountries().first().size)
    }

    @Test
    fun `cache is still served when the network fails (offline-first)`() = runTest {
        val dao = FakeCountryCacheDao(listOf(cached("FRA", name = "France")))
        val api = FakeCountryApi().apply { allResult = { throw IOException("no network") } }
        val repo = CountryRepositoryImpl(api, dao)

        val result = repo.refresh()

        assertTrue(result.isFailure)
        val cached = repo.observeCountries().first()
        assertEquals(1, cached.size)
        assertEquals("France", cached.first().commonName)
    }

    @Test
    fun `ensureDetail fills detail fields in the cache`() = runTest {
        val dao = FakeCountryCacheDao(listOf(cached("FRA", detailUpdatedAt = 0L)))
        val api = FakeCountryApi().apply { byCodeResult = { listOf(dto("FRA")) } }
        val repo = CountryRepositoryImpl(api, dao)

        val result = repo.ensureDetail("FRA")

        assertTrue(result.isSuccess)
        assertTrue((dao.getByCode("FRA")?.detailUpdatedAt ?: 0L) > 0L)
    }
}
