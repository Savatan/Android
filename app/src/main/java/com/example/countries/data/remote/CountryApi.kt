package com.example.countries.data.remote

import com.example.countries.data.remote.dto.CountryDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CountryApi {

    @GET("v3.1/all")
    suspend fun getAllCountries(
        @Query("fields") fields: String = SUMMARY_FIELDS
    ): List<CountryDto>

    @GET("v3.1/name/{name}")
    suspend fun searchByName(
        @Path("name") name: String,
        @Query("fields") fields: String = SUMMARY_FIELDS
    ): List<CountryDto>

    @GET("v3.1/alpha")
    suspend fun getByCode(
        @Query("codes") code: String,
        @Query("fields") fields: String = DETAIL_FIELDS
    ): List<CountryDto>

    companion object {
        const val BASE_URL = "https://restcountries.com/"
        private const val SUMMARY_FIELDS = "name,cca3,flags,region,capital,population"
        private const val DETAIL_FIELDS =
            "name,cca3,flags,region,subregion,capital,population,area,languages,currencies,timezones,maps"
    }
}
