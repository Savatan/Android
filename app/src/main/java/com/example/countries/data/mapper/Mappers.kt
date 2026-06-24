package com.example.countries.data.mapper

import com.example.countries.data.local.CachedCountryEntity
import com.example.countries.data.local.CollectionWithCount
import com.example.countries.data.local.CountryNoteEntity
import com.example.countries.data.remote.dto.CountryDto
import com.example.countries.domain.model.CollectionSummary
import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.model.Note

private const val SEPARATOR = "|"

private fun List<String>.toCsv(): String = joinToString(SEPARATOR)

private fun String.fromCsv(): List<String> =
    if (isBlank()) emptyList() else split(SEPARATOR)

fun CountryDto.toSummaryEntity(existing: CachedCountryEntity?, now: Long): CachedCountryEntity? {
    val code = cca3 ?: return null
    val common = name?.common ?: return null
    return CachedCountryEntity(
        code = code,
        commonName = common,
        officialName = name.official ?: existing?.officialName ?: "",
        flagUrl = flags?.png ?: flags?.svg ?: existing?.flagUrl ?: "",
        region = region ?: existing?.region ?: "",
        subregion = existing?.subregion ?: "",
        capital = capital?.firstOrNull() ?: existing?.capital ?: "",
        population = population ?: existing?.population ?: 0L,
        area = existing?.area ?: 0.0,
        languagesCsv = existing?.languagesCsv ?: "",
        currenciesCsv = existing?.currenciesCsv ?: "",
        timezonesCsv = existing?.timezonesCsv ?: "",
        summaryUpdatedAt = now,
        detailUpdatedAt = existing?.detailUpdatedAt ?: 0L
    )
}

fun CountryDto.toDetailEntity(existing: CachedCountryEntity?, now: Long): CachedCountryEntity? {
    val code = cca3 ?: existing?.code ?: return null
    val common = name?.common ?: existing?.commonName ?: return null
    val languages = (languages?.values?.sorted() ?: emptyList()).toCsv()
    val currencies = (currencies?.values?.mapNotNull { it.name } ?: emptyList()).toCsv()
    val zones = (timezones ?: emptyList()).toCsv()
    return CachedCountryEntity(
        code = code,
        commonName = common,
        officialName = name?.official ?: existing?.officialName ?: "",
        flagUrl = flags?.png ?: flags?.svg ?: existing?.flagUrl ?: "",
        region = region ?: existing?.region ?: "",
        subregion = subregion ?: existing?.subregion ?: "",
        capital = capital?.joinToString(", ") ?: existing?.capital ?: "",
        population = population ?: existing?.population ?: 0L,
        area = area ?: existing?.area ?: 0.0,
        languagesCsv = languages.ifBlank { existing?.languagesCsv ?: "" },
        currenciesCsv = currencies.ifBlank { existing?.currenciesCsv ?: "" },
        timezonesCsv = zones.ifBlank { existing?.timezonesCsv ?: "" },
        summaryUpdatedAt = existing?.summaryUpdatedAt ?: now,
        detailUpdatedAt = now
    )
}

fun CachedCountryEntity.toSummary(): CountrySummary = CountrySummary(
    code = code,
    commonName = commonName,
    flagUrl = flagUrl,
    region = region,
    capital = capital,
    population = population
)

fun CachedCountryEntity.toCountry(): Country = Country(
    code = code,
    commonName = commonName,
    officialName = officialName,
    flagUrl = flagUrl,
    flagAlt = "",
    region = region,
    subregion = subregion,
    capital = capital,
    population = population,
    area = area,
    languages = languagesCsv.fromCsv(),
    currencies = currenciesCsv.fromCsv(),
    timezones = timezonesCsv.fromCsv(),
    mapsUrl = ""
)

fun CollectionWithCount.toSummary(): CollectionSummary = CollectionSummary(
    id = id,
    name = name,
    itemCount = itemCount,
    createdAt = createdAt
)

fun CountryNoteEntity.toNote(): Note = Note(
    countryCode = countryCode,
    text = text,
    updatedAt = updatedAt
)
