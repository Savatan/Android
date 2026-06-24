package com.example.countries.fake

import com.example.countries.data.local.FavouriteCountryDao
import com.example.countries.data.local.FavouriteCountryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavouriteDao : FavouriteCountryDao {

    private val state = MutableStateFlow<List<FavouriteCountryEntity>>(emptyList())

    override fun observeAll(): Flow<List<FavouriteCountryEntity>> = state

    override fun observeCodes(): Flow<List<String>> = state.map { list -> list.map { it.code } }

    override suspend fun insert(entity: FavouriteCountryEntity) {
        state.update { current -> current.filterNot { it.code == entity.code } + entity }
    }

    override suspend fun deleteByCode(code: String) {
        state.update { current -> current.filterNot { it.code == code } }
    }
}
