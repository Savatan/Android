package com.example.countries.domain.model

enum class Region(val label: String, val apiName: String) {
    ALL("All", ""),
    AFRICA("Africa", "Africa"),
    AMERICAS("Americas", "Americas"),
    ASIA("Asia", "Asia"),
    EUROPE("Europe", "Europe"),
    OCEANIA("Oceania", "Oceania")
}
