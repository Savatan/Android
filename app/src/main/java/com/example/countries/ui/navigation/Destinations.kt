package com.example.countries.ui.navigation

object Destinations {
    const val LIST = "list"
    const val FAVOURITES = "favourites"

    const val DETAIL_ARG_CODE = "code"
    const val DETAIL_ROUTE = "detail/{$DETAIL_ARG_CODE}"

    fun detail(code: String): String = "detail/$code"
}
