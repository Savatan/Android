package com.example.countries.domain.repository

import com.example.countries.domain.model.CountrySummary
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    fun observeRecent(limit: Int): Flow<List<CountrySummary>>

    suspend fun recordView(code: String)

    suspend fun clear()
}
