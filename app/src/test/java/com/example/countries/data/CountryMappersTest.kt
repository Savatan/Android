package com.example.countries.data

import com.example.countries.data.local.FavouriteCountryEntity
import com.example.countries.data.mapper.toCountryOrNull
import com.example.countries.data.mapper.toEntity
import com.example.countries.data.mapper.toSummary
import com.example.countries.data.mapper.toSummaryOrNull
import com.example.countries.data.remote.dto.CountryDto
import com.example.countries.data.remote.dto.CurrencyDto
import com.example.countries.data.remote.dto.FlagsDto
import com.example.countries.data.remote.dto.NameDto
import com.example.countries.util.summary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CountryMappersTest {

    private fun dto(
        cca3: String? = "FRA",
        common: String? = "France"
    ) = CountryDto(
        name = NameDto(common = common, official = "French Republic"),
        cca3 = cca3,
        flags = FlagsDto(png = "https://flags/fr.png", svg = "https://flags/fr.svg", alt = "Flag"),
        region = "Europe",
        subregion = "Western Europe",
        capital = listOf("Paris"),
        population = 67_000_000,
        area = 551695.0,
        languages = mapOf("fra" to "French"),
        currencies = mapOf("EUR" to CurrencyDto(name = "Euro", symbol = "€")),
        timezones = listOf("UTC+01:00"),
        maps = null
    )

    @Test
    fun `toSummaryOrNull maps required fields`() {
        val result = dto().toSummaryOrNull()

        assertEquals("FRA", result?.code)
        assertEquals("France", result?.commonName)
        assertEquals("Paris", result?.capital)
        assertEquals("Europe", result?.region)
        assertEquals(67_000_000, result?.population)
        assertEquals("https://flags/fr.png", result?.flagUrl)
    }

    @Test
    fun `toSummaryOrNull returns null when code is missing`() {
        assertNull(dto(cca3 = null).toSummaryOrNull())
    }

    @Test
    fun `toSummaryOrNull returns null when name is missing`() {
        assertNull(dto(common = null).toSummaryOrNull())
    }

    @Test
    fun `toCountryOrNull maps languages and currencies`() {
        val result = dto().toCountryOrNull()

        assertEquals("French Republic", result?.officialName)
        assertEquals(listOf("French"), result?.languages)
        assertEquals(listOf("Euro"), result?.currencies)
        assertEquals("Western Europe", result?.subregion)
    }

    @Test
    fun `summary to entity and back is lossless`() {
        val original = summary(code = "ITA", name = "Italy")
        val restored: com.example.countries.domain.model.CountrySummary =
            original.toEntity().toSummary()

        assertEquals(original, restored)
    }

    @Test
    fun `entity to summary maps fields`() {
        val entity = FavouriteCountryEntity(
            code = "ESP",
            commonName = "Spain",
            flagUrl = "https://flags/es.png",
            region = "Europe",
            capital = "Madrid",
            population = 47_000_000
        )

        val result = entity.toSummary()

        assertEquals("ESP", result.code)
        assertEquals("Spain", result.commonName)
        assertEquals("Madrid", result.capital)
    }
}
