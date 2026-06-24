package com.example.countries.util

import com.example.countries.domain.model.Country
import com.example.countries.domain.model.CountrySummary

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
    name: String = code
): Country = Country(
    code = code,
    commonName = name,
    officialName = "Official $name",
    flagUrl = "https://flags.example/$code.png",
    flagAlt = "Flag of $name",
    region = "Europe",
    subregion = "Western Europe",
    capital = "Capital-$code",
    population = 1_000_000,
    area = 100.0,
    languages = listOf("Lang"),
    currencies = listOf("Currency"),
    timezones = listOf("UTC+01:00"),
    mapsUrl = "https://maps.example/$code"
)
