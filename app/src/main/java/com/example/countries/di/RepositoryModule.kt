package com.example.countries.di

import com.example.countries.data.repository.CountryRepositoryImpl
import com.example.countries.domain.repository.CountryRepository
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
}
