package com.example.countries.data.mapper

import com.example.countries.data.local.FavouriteCountryEntity
import com.example.countries.data.remote.dto.CountryDto
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary

fun CountryDto.toSummaryOrNull(): CountrySummary? {
    val code = cca3 ?: return null
    val common = name?.common ?: return null
    return CountrySummary(
        code = code,
        commonName = common,
        flagUrl = flags?.png ?: flags?.svg.orEmpty(),
        region = region.orEmpty(),
        capital = capital?.firstOrNull().orEmpty(),
        population = population ?: 0L
    )
}

fun CountryDto.toCountryOrNull(): Country? {
    val code = cca3 ?: return null
    val common = name?.common ?: return null
    return Country(
        code = code,
        commonName = common,
        officialName = name.official.orEmpty(),
        flagUrl = flags?.png ?: flags?.svg.orEmpty(),
        flagAlt = flags?.alt.orEmpty(),
        region = region.orEmpty(),
        subregion = subregion.orEmpty(),
        capital = capital?.joinToString(", ").orEmpty(),
        population = population ?: 0L,
        area = area ?: 0.0,
        languages = languages?.values?.sorted() ?: emptyList(),
        currencies = currencies?.values?.mapNotNull { it.name } ?: emptyList(),
        timezones = timezones ?: emptyList(),
        mapsUrl = maps?.googleMaps.orEmpty()
    )
}

fun CountrySummary.toEntity(): FavouriteCountryEntity = FavouriteCountryEntity(
    code = code,
    commonName = commonName,
    flagUrl = flagUrl,
    region = region,
    capital = capital,
    population = population
)

fun FavouriteCountryEntity.toSummary(): CountrySummary = CountrySummary(
    code = code,
    commonName = commonName,
    flagUrl = flagUrl,
    region = region,
    capital = capital,
    population = population
)
