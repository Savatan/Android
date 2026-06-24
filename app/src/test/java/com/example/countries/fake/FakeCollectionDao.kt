package com.example.countries.fake

import com.example.countries.data.local.CachedCountryEntity
import com.example.countries.data.local.CollectionDao
import com.example.countries.data.local.CollectionEntity
import com.example.countries.data.local.CollectionItemEntity
import com.example.countries.data.local.CollectionWithCount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCollectionDao(
    cache: List<CachedCountryEntity> = emptyList()
) : CollectionDao {

    private val collections = MutableStateFlow<Map<Long, CollectionEntity>>(emptyMap())
    private val items = MutableStateFlow<List<CollectionItemEntity>>(emptyList())
    private val cacheByCode = cache.associateBy { it.code }
    private var nextId = 1L

    override fun observeCollectionsWithCount(): Flow<List<CollectionWithCount>> =
        combine(collections, items) { cols, its ->
            cols.values.sortedByDescending { it.createdAt }.map { c ->
                CollectionWithCount(c.id, c.name, c.createdAt, its.count { it.collectionId == c.id })
            }
        }

    override fun observeCollection(id: Long): Flow<CollectionEntity?> =
        collections.map { it[id] }

    override fun observeItems(collectionId: Long): Flow<List<CachedCountryEntity>> =
        items.map { list ->
            list.filter { it.collectionId == collectionId }
                .sortedByDescending { it.addedAt }
                .mapNotNull { cacheByCode[it.countryCode] }
        }

    override fun observeCollectionsForCountry(code: String): Flow<List<CollectionEntity>> =
        combine(collections, items) { cols, its ->
            its.filter { it.countryCode == code }
                .mapNotNull { cols[it.collectionId] }
                .sortedBy { it.name }
        }

    override suspend fun insertCollection(collection: CollectionEntity): Long {
        val id = if (collection.id != 0L) collection.id else nextId++
        collections.update { it + (id to collection.copy(id = id)) }
        return id
    }

    override suspend fun deleteCollection(id: Long) {
        collections.update { it - id }
    }

    override suspend fun deleteItemsForCollection(id: Long) {
        items.update { list -> list.filterNot { it.collectionId == id } }
    }

    override suspend fun insertItem(item: CollectionItemEntity) {
        items.update { list ->
            list.filterNot { it.collectionId == item.collectionId && it.countryCode == item.countryCode } + item
        }
    }

    override suspend fun deleteItem(collectionId: Long, code: String) {
        items.update { list ->
            list.filterNot { it.collectionId == collectionId && it.countryCode == code }
        }
    }

    override suspend fun countItem(collectionId: Long, code: String): Int =
        items.value.count { it.collectionId == collectionId && it.countryCode == code }
}
