package com.example.countries.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.countries.domain.model.Region
import com.example.countries.domain.model.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val intervals = listOf(3, 6, 12, 24)

    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Theme", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ThemeMode.entries.size) { index ->
                    val mode = ThemeMode.entries[index]
                    FilterChip(
                        selected = prefs.themeMode == mode,
                        onClick = { viewModel.onThemeSelected(mode) },
                        label = { Text(mode.label) }
                    )
                }
            }

            HorizontalDivider()

            Text("Default region", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(Region.entries.size) { index ->
                    val region = Region.entries[index]
                    FilterChip(
                        selected = prefs.region == region,
                        onClick = { viewModel.onRegionSelected(region) },
                        label = { Text(region.label) }
                    )
                }
            }

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Background refresh", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Keep offline data up to date automatically",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Switch(
                    checked = prefs.autoRefresh,
                    onCheckedChange = viewModel::onAutoRefreshChanged
                )
            }

            Text("Refresh interval (hours)", style = MaterialTheme.typography.bodyMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(intervals.size) { index ->
                    val hours = intervals[index]
                    FilterChip(
                        selected = prefs.refreshIntervalHours == hours,
                        onClick = { viewModel.onIntervalSelected(hours) },
                        label = { Text("$hours") }
                    )
                }
            }
        }
    }
}
