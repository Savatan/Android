package com.example.countries.fake

import com.example.countries.data.remote.CountryApi
import com.example.countries.data.remote.dto.CountryDto

class FakeCountryApi : CountryApi {

    var allResult: () -> List<CountryDto> = { emptyList() }
    var byCodeResult: (String) -> List<CountryDto> = { emptyList() }
    var allCallCount: Int = 0
        private set

    override suspend fun getAllCountries(fields: String): List<CountryDto> {
        allCallCount++
        return allResult()
    }

    override suspend fun searchByName(name: String, fields: String): List<CountryDto> = emptyList()

    override suspend fun getByCode(code: String, fields: String): List<CountryDto> = byCodeResult(code)
}
