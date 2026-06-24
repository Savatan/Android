package com.example.countries.fake

import com.example.countries.data.remote.CountryApi
import com.example.countries.data.remote.dto.CountryDto

class FakeApi : CountryApi {
    override suspend fun getAllCountries(fields: String): List<CountryDto> = emptyList()
    override suspend fun searchByName(name: String, fields: String): List<CountryDto> = emptyList()
    override suspend fun getByCode(code: String, fields: String): List<CountryDto> = emptyList()
}
