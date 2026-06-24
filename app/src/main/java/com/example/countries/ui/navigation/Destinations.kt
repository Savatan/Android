package com.example.countries.ui.navigation

object Destinations {
    const val LIST = "list"
    const val COLLECTIONS = "collections"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val NOTES = "notes"

    const val DETAIL_ARG_CODE = "code"
    const val DETAIL_ROUTE = "detail/{$DETAIL_ARG_CODE}"
    fun detail(code: String): String = "detail/$code"

    const val COLLECTION_ARG_ID = "collectionId"
    const val COLLECTION_ROUTE = "collection/{$COLLECTION_ARG_ID}"
    fun collection(id: Long): String = "collection/$id"
}
