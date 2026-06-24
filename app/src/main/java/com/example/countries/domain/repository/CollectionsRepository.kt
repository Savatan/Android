package com.example.countries.domain.repository

import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.CountrySummary
import kotlinx.coroutines.flow.Flow

interface CollectionsRepository {

    fun observeCollections(): Flow<List<CollectionSummary>>

    fun observeCollection(id: Long): Flow<CollectionSummary?>

    fun observeCollectionCountries(id: Long): Flow<List<CountrySummary>>

    fun observeCollectionsForCountry(code: String): Flow<List<CollectionSummary>>

    suspend fun createCollection(name: String): Long

    suspend fun deleteCollection(id: Long)

    suspend fun addToCollection(collectionId: Long, code: String)

    suspend fun removeFromCollection(collectionId: Long, code: String)
}
