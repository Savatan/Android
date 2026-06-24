package com.example.countries.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.countries.domain.model.CountrySummary
import com.example.countries.domain.model.Region
import com.example.countries.ui.components.EmptyView
import com.example.countries.ui.components.ErrorView
import com.example.countries.ui.components.LoadingView
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryListScreen(
    onCountryClick: (String) -> Unit,
    onNotesClick: () -> Unit,
    viewModel: CountryListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Countries") },
                actions = {
                    IconButton(onClick = onNotesClick) {
                        Icon(Icons.AutoMirrored.Filled.NoteAdd, contentDescription = "Notes")
                    }
                    IconButton(onClick = viewModel::onRefresh) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true,
                label = { Text("Search by name") }
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(Region.entries) { region ->
                    FilterChip(
                        selected = region == state.region,
                        onClick = { viewModel.onSelectRegion(region) },
                        label = { Text(region.label) }
                    )
                }
            }

            SyncCaption(lastSync = state.lastSync, isRefreshing = state.isRefreshing)

            Box(modifier = Modifier.fillMaxSize()) {
                when (val content = state.content) {
                    is ListContent.Loading -> LoadingView()
                    is ListContent.Error -> ErrorView(content.message, onRetry = viewModel::onRetry)
                    is ListContent.Empty -> EmptyView("No countries found.")
                    is ListContent.Success -> CountryList(
                        countries = content.countries,
                        noteCodes = state.noteCodes,
                        onCountryClick = onCountryClick
                    )
                }
            }
        }
    }
}

@Composable
private fun SyncCaption(lastSync: Long?, isRefreshing: Boolean) {
    val text = when {
        isRefreshing -> "Updating…"
        lastSync != null -> "Offline-ready · updated ${DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(lastSync))}"
        else -> "No data yet"
    }
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun CountryList(
    countries: List<CountrySummary>,
    noteCodes: Set<String>,
    onCountryClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(countries, key = { it.code }) { country ->
            CountryRow(
                country = country,
                hasNote = noteCodes.contains(country.code),
                onClick = { onCountryClick(country.code) }
            )
        }
    }
}

@Composable
private fun CountryRow(
    country: CountrySummary,
    hasNote: Boolean,
    onClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = country.flagUrl,
                contentDescription = "${country.commonName} flag",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 56.dp, height = 40.dp).clip(RoundedCornerShape(6.dp))
            )
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Text(
                    text = country.commonName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = listOf(country.region, country.capital)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (hasNote) {
                Text(
                    text = "Note",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
