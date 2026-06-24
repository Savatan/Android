package com.example.countries.fake

import com.example.countries.data.local.CachedCountryEntity
import com.example.countries.data.local.CountryCacheDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCountryCacheDao(
    initial: List<CachedCountryEntity> = emptyList()
) : CountryCacheDao {

    private val state = MutableStateFlow(initial.associateBy { it.code })

    override fun observeAll(): Flow<List<CachedCountryEntity>> =
        state.map { it.values.sortedBy { c -> c.commonName } }

    override fun observeByCode(code: String): Flow<CachedCountryEntity?> =
        state.map { it[code] }

    override suspend fun getByCode(code: String): CachedCountryEntity? = state.value[code]

    override fun observeLastSync(): Flow<Long?> =
        state.map { map -> map.values.maxOfOrNull { it.summaryUpdatedAt } }

    override suspend fun lastSync(): Long? = state.value.values.maxOfOrNull { it.summaryUpdatedAt }

    override suspend fun count(): Int = state.value.size

    override suspend fun getAllOnce(): List<CachedCountryEntity> = state.value.values.toList()

    override suspend fun upsertAll(items: List<CachedCountryEntity>) {
        state.update { current -> current + items.associateBy { it.code } }
    }

    override suspend fun upsert(item: CachedCountryEntity) {
        state.update { current -> current + (item.code to item) }
    }
}
