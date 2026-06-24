package com.example.countries.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.countries.ui.detail.CountryDetailScreen
import com.example.countries.ui.favourites.FavouritesScreen
import com.example.countries.ui.list.CountryListScreen

@Composable
fun CountriesNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.LIST
    ) {
        composable(Destinations.LIST) {
            CountryListScreen(
                onCountryClick = { code -> navController.navigate(Destinations.detail(code)) },
                onFavouritesClick = { navController.navigate(Destinations.FAVOURITES) }
            )
        }

        composable(
            route = Destinations.DETAIL_ROUTE,
            arguments = listOf(navArgument(Destinations.DETAIL_ARG_CODE) { type = NavType.StringType })
        ) {
            CountryDetailScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Destinations.FAVOURITES) {
            FavouritesScreen(
                onCountryClick = { code -> navController.navigate(Destinations.detail(code)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
