package com.example.countries.di

import com.example.countries.data.repository.CollectionsRepositoryImpl
import com.example.countries.data.repository.CountryRepositoryImpl
import com.example.countries.data.repository.HistoryRepositoryImpl
import com.example.countries.data.repository.NotesRepositoryImpl
import com.example.countries.data.repository.SettingsRepositoryImpl
import com.example.countries.domain.repository.CollectionsRepository
import com.example.countries.domain.repository.CountryRepository
import com.example.countries.domain.repository.HistoryRepository
import com.example.countries.domain.repository.NotesRepository
import com.example.countries.domain.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCountryRepository(impl: CountryRepositoryImpl): CountryRepository

    @Binds
    @Singleton
    abstract fun bindNotesRepository(impl: NotesRepositoryImpl): NotesRepository

    @Binds
    @Singleton
    abstract fun bindCollectionsRepository(impl: CollectionsRepositoryImpl): CollectionsRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository
}
