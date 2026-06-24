package com.example.countries.data.repository

import com.example.countries.data.local.CollectionDao
import com.example.countries.data.local.CollectionEntity
import com.example.countries.data.local.CollectionItemEntity
import com.example.countries.data.mapper.toSummary
import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.repository.CollectionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CollectionsRepositoryImpl @Inject constructor(
    private val dao: CollectionDao
) : CollectionsRepository {

    override fun observeCollections(): Flow<List<CollectionSummary>> =
        dao.observeCollectionsWithCount().map { list -> list.map { it.toSummary() } }

    override fun observeCollection(id: Long): Flow<CollectionSummary?> =
        dao.observeCollectionsWithCount().map { list -> list.firstOrNull { it.id == id }?.toSummary() }

    override fun observeCollectionCountries(id: Long): Flow<List<CountrySummary>> =
        dao.observeItems(id).map { list -> list.map { it.toSummary() } }

    override fun observeCollectionsForCountry(code: String): Flow<List<CollectionSummary>> =
        dao.observeCollectionsForCountry(code).map { list ->
            list.map { CollectionSummary(it.id, it.name, 0, it.createdAt) }
        }

    override suspend fun createCollection(name: String): Long {
        val trimmed = name.trim().ifBlank { "Untitled" }
        return dao.insertCollection(CollectionEntity(name = trimmed, createdAt = System.currentTimeMillis()))
    }

    override suspend fun deleteCollection(id: Long) {
        dao.deleteItemsForCollection(id)
        dao.deleteCollection(id)
    }

    override suspend fun addToCollection(collectionId: Long, code: String) {
        if (dao.countItem(collectionId, code) == 0) {
            dao.insertItem(CollectionItemEntity(collectionId, code, System.currentTimeMillis()))
        }
    }

    override suspend fun removeFromCollection(collectionId: Long, code: String) {
        dao.deleteItem(collectionId, code)
    }
}
