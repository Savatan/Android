package com.example.countries.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColors = darkColorScheme(
    primary = Green80,
    secondary = GreenGrey80,
    tertiary = Sand80
)

private val LightColors = lightColorScheme(
    primary = Green40,
    secondary = GreenGrey40,
    tertiary = Sand40
)

@Composable
fun CountriesExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
