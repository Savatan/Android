package com.example.countries.util

import com.example.countries.data.local.CachedCountryEntity
import com.example.countries.data.remote.dto.CountryDto
import com.example.countries.data.remote.dto.FlagsDto
import com.example.countries.data.remote.dto.NameDto
import com.example.countries.domain.model.CountrySummary

fun dto(
    code: String,
    name: String = code,
    region: String = "Europe",
    capital: String = "Capital-$code",
    population: Long = 1_000_000
): CountryDto = CountryDto(
    name = NameDto(common = name, official = "Official $name"),
    cca3 = code,
    flags = FlagsDto(png = "https://flags.example/$code.png", svg = null, alt = null),
    region = region,
    subregion = "Sub-$region",
    capital = listOf(capital),
    population = population,
    area = 100.0,
    languages = mapOf("lng" to "Language-$code"),
    currencies = null,
    timezones = listOf("UTC"),
    maps = null
)

fun cached(
    code: String,
    name: String = code,
    region: String = "Europe",
    capital: String = "Capital-$code",
    population: Long = 1_000_000,
    summaryUpdatedAt: Long = 1_000L,
    detailUpdatedAt: Long = 0L
): CachedCountryEntity = CachedCountryEntity(
    code = code,
    commonName = name,
    officialName = "Official $name",
    flagUrl = "https://flags.example/$code.png",
    region = region,
    subregion = "Sub-$region",
    capital = capital,
    population = population,
    area = 100.0,
    languagesCsv = "Language-$code",
    currenciesCsv = "",
    timezonesCsv = "UTC",
    summaryUpdatedAt = summaryUpdatedAt,
    detailUpdatedAt = detailUpdatedAt
)

fun summary(
    code: String,
    name: String = code,
    region: String = "Europe"
): CountrySummary = CountrySummary(
    code = code,
    commonName = name,
    flagUrl = "https://flags.example/$code.png",
    region = region,
    capital = "Capital-$code",
    population = 1_000_000
)

fun country(
    code: String,
    name: String = code,
    region: String = "Europe"
): com.example.countries.domain.model.Country = com.example.countries.domain.model.Country(
    code = code,
    commonName = name,
    officialName = "Official $name",
    flagUrl = "https://flags.example/$code.png",
    flagAlt = "",
    region = region,
    subregion = "Sub-$region",
    capital = "Capital-$code",
    population = 1_000_000,
    area = 100.0,
    languages = listOf("Language-$code"),
    currencies = emptyList(),
    timezones = listOf("UTC"),
    mapsUrl = ""
)
