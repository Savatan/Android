package com.example.countries.data.remote.dto

data class CountryDto(
    val name: NameDto?,
    val cca3: String?,
    val flags: FlagsDto?,
    val region: String?,
    val subregion: String?,
    val capital: List<String>?,
    val population: Long?,
    val area: Double?,
    val languages: Map<String, String>?,
    val currencies: Map<String, CurrencyDto>?,
    val timezones: List<String>?,
    val maps: MapsDto?
)

data class NameDto(
    val common: String?,
    val official: String?
)

data class FlagsDto(
    val png: String?,
    val svg: String?,
    val alt: String?
)

data class CurrencyDto(
    val name: String?,
    val symbol: String?
)

data class MapsDto(
    val googleMaps: String?,
    val openStreetMaps: String?
)
