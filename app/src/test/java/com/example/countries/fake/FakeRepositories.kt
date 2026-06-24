package com.example.countries.fake

import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.model.Note
import com.example.countries.domain.model.Region
import com.example.countries.domain.model.ThemeMode
import com.example.countries.domain.model.UserPreferences
import com.example.countries.domain.repository.CollectionsRepository
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.domain.repository.HistoryRepository
import com.example.countries.domain.repository.NotesRepository
import com.example.countries.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeCountryRepository(
    countries: List<CountrySummary> = emptyList()
) : CountryRepository {

    val countriesState = MutableStateFlow(countries)
    val countryState = MutableStateFlow<Country?>(null)
    val lastSyncState = MutableStateFlow<Long?>(null)
    var refreshResult: Result<Unit> = Result.success(Unit)
    var refreshCount = 0
        private set
    var ensureDetailCount = 0
        private set
    var cachePresent = true

    override fun observeCountries(): Flow<List<CountrySummary>> = countriesState

    override fun observeCountry(code: String): Flow<Country?> = countryState

    override fun observeLastSync(): Flow<Long?> = lastSyncState

    override suspend fun hasCache(): Boolean = cachePresent

    override suspend fun isStale(ttlMillis: Long, now: Long): Boolean =
        (lastSyncState.value ?: 0L) < now - ttlMillis

    override suspend fun refresh(): Result<Unit> {
        refreshCount++
        return refreshResult
    }

    override suspend fun ensureDetail(code: String): Result<Unit> {
        ensureDetailCount++
        return Result.success(Unit)
    }
}

class FakeNotesRepository : NotesRepository {

    val notesState = MutableStateFlow<List<Note>>(emptyList())

    override fun observeNotes(): Flow<List<Note>> = notesState

    override fun observeNote(code: String): Flow<Note?> =
        notesState.map { list -> list.firstOrNull { it.countryCode == code } }

    override suspend fun saveNote(code: String, text: String) {
        val trimmed = text.trim()
        notesState.update { list ->
            val without = list.filterNot { it.countryCode == code }
            if (trimmed.isEmpty()) without else without + Note(code, trimmed, 1L)
        }
    }

    override suspend fun deleteNote(code: String) {
        notesState.update { list -> list.filterNot { it.countryCode == code } }
    }
}

class FakeCollectionsRepository : CollectionsRepository {

    val collectionsState = MutableStateFlow<List<CollectionSummary>>(emptyList())
    private val membership = MutableStateFlow<Map<Long, Set<String>>>(emptyMap())
    private var nextId = 1L

    override fun observeCollections(): Flow<List<CollectionSummary>> = collectionsState

    override fun observeCollection(id: Long): Flow<CollectionSummary?> =
        collectionsState.map { list -> list.firstOrNull { it.id == id } }

    override fun observeCollectionCountries(id: Long): Flow<List<CountrySummary>> =
        MutableStateFlow(emptyList())

    override fun observeCollectionsForCountry(code: String): Flow<List<CollectionSummary>> =
        membership.map { map ->
            collectionsState.value.filter { map[it.id]?.contains(code) == true }
        }

    override suspend fun createCollection(name: String): Long {
        val id = nextId++
        collectionsState.update { it + CollectionSummary(id, name.trim(), 0, id) }
        return id
    }

    override suspend fun deleteCollection(id: Long) {
        collectionsState.update { list -> list.filterNot { it.id == id } }
        membership.update { it - id }
    }

    override suspend fun addToCollection(collectionId: Long, code: String) {
        membership.update { map ->
            val set = map[collectionId].orEmpty() + code
            map + (collectionId to set)
        }
    }

    override suspend fun removeFromCollection(collectionId: Long, code: String) {
        membership.update { map ->
            val set = map[collectionId].orEmpty() - code
            map + (collectionId to set)
        }
    }
}

class FakeHistoryRepository : HistoryRepository {

    val recentState = MutableStateFlow<List<CountrySummary>>(emptyList())
    var recordCount = 0
        private set
    var lastRecorded: String? = null
        private set

    override fun observeRecent(limit: Int): Flow<List<CountrySummary>> = recentState

    override suspend fun recordView(code: String) {
        recordCount++
        lastRecorded = code
    }

    override suspend fun clear() {
        recentState.value = emptyList()
    }
}

class FakeSettingsRepository : SettingsRepository {

    val state = MutableStateFlow(UserPreferences())

    override val preferences: Flow<UserPreferences> = state

    override suspend fun setThemeMode(mode: ThemeMode) {
        state.update { it.copy(themeMode = mode) }
    }

    override suspend fun setRegion(region: Region) {
        state.update { it.copy(region = region) }
    }

    override suspend fun setAutoRefresh(enabled: Boolean) {
        state.update { it.copy(autoRefresh = enabled) }
    }

    override suspend fun setRefreshIntervalHours(hours: Int) {
        state.update { it.copy(refreshIntervalHours = hours) }
    }
}
