package com.example.countries.domain.model

data class CountrySummary(
    val code: String,
    val commonName: String,
    val flagUrl: String,
    val region: String,
    val capital: String,
    val population: Long
)
