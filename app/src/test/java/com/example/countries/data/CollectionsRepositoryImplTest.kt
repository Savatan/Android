package com.example.countries.data

import com.example.countries.data.repository.CollectionsRepositoryImpl
import com.example.countries.fake.FakeCollectionDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CollectionsRepositoryImplTest {

    @Test
    fun `creating a collection and adding the same country twice keeps one item`() = runTest {
        val repo = CollectionsRepositoryImpl(FakeCollectionDao())

        val id = repo.createCollection("Wishlist")
        repo.addToCollection(id, "FRA")
        repo.addToCollection(id, "FRA")

        val collection = repo.observeCollections().first().first { it.id == id }
        assertEquals(1, collection.itemCount)
    }

    @Test
    fun `removing a country updates the count`() = runTest {
        val repo = CollectionsRepositoryImpl(FakeCollectionDao())

        val id = repo.createCollection("Wishlist")
        repo.addToCollection(id, "FRA")
        repo.addToCollection(id, "ITA")
        repo.removeFromCollection(id, "FRA")

        val collection = repo.observeCollections().first().first { it.id == id }
        assertEquals(1, collection.itemCount)
    }

    @Test
    fun `observeCollectionsForCountry returns collections that contain the country`() = runTest {
        val repo = CollectionsRepositoryImpl(FakeCollectionDao())

        val a = repo.createCollection("A")
        val b = repo.createCollection("B")
        repo.addToCollection(a, "FRA")
        repo.addToCollection(b, "ITA")

        val forFrance = repo.observeCollectionsForCountry("FRA").first()
        assertEquals(1, forFrance.size)
        assertEquals(a, forFrance.first().id)
    }
}
