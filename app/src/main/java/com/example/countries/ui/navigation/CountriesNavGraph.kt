package com.example.countries.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.countries.ui.collections.CollectionDetailScreen
import com.example.countries.ui.collections.CollectionsScreen
import com.example.countries.ui.detail.CountryDetailScreen
import com.example.countries.ui.history.HistoryScreen
import com.example.countries.ui.list.CountryListScreen
import com.example.countries.ui.notes.NotesScreen
import com.example.countries.ui.settings.SettingsScreen

private data class TopLevel(val route: String, val label: String, val icon: ImageVector)

private val topLevelDestinations = listOf(
    TopLevel(Destinations.LIST, "Countries", Icons.AutoMirrored.Filled.List),
    TopLevel(Destinations.COLLECTIONS, "Collections", Icons.Filled.CollectionsBookmark),
    TopLevel(Destinations.HISTORY, "History", Icons.Filled.History),
    TopLevel(Destinations.SETTINGS, "Settings", Icons.Filled.Settings)
)

@Composable
fun CountriesNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = topLevelDestinations.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val currentDestination = backStackEntry?.destination
                    topLevelDestinations.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LIST,
            modifier = androidx.compose.ui.Modifier.padding(innerPadding)
        ) {
            composable(Destinations.LIST) {
                CountryListScreen(
                    onCountryClick = { navController.navigate(Destinations.detail(it)) },
                    onNotesClick = { navController.navigate(Destinations.NOTES) }
                )
            }
            composable(Destinations.COLLECTIONS) {
                CollectionsScreen(
                    onCollectionClick = { navController.navigate(Destinations.collection(it)) }
                )
            }
            composable(Destinations.HISTORY) {
                HistoryScreen(
                    onCountryClick = { navController.navigate(Destinations.detail(it)) }
                )
            }
            composable(Destinations.SETTINGS) {
                SettingsScreen()
            }
            composable(Destinations.NOTES) {
                NotesScreen(
                    onBack = { navController.popBackStack() },
                    onCountryClick = { navController.navigate(Destinations.detail(it)) }
                )
            }
            composable(
                route = Destinations.DETAIL_ROUTE,
                arguments = listOf(navArgument(Destinations.DETAIL_ARG_CODE) { type = NavType.StringType })
            ) {
                CountryDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = Destinations.COLLECTION_ROUTE,
                arguments = listOf(navArgument(Destinations.COLLECTION_ARG_ID) { type = NavType.LongType })
            ) {
                CollectionDetailScreen(
                    onBack = { navController.popBackStack() },
                    onCountryClick = { navController.navigate(Destinations.detail(it)) }
                )
            }
        }
    }
}
