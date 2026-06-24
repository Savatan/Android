package com.example.countries.domain.model

data class Country(
    val code: String,
    val commonName: String,
    val officialName: String,
    val flagUrl: String,
    val flagAlt: String,
    val region: String,
    val subregion: String,
    val capital: String,
    val population: Long,
    val area: Double,
    val languages: List<String>,
    val currencies: List<String>,
    val timezones: List<String>,
    val mapsUrl: String
)
