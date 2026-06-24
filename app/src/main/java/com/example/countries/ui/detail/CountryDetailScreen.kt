package com.example.countries.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.countries.domain.model.Country
import com.example.countries.ui.components.LoadingView
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    onBack: () -> Unit,
    viewModel: CountryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.country?.commonName ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        val country = state.country
        if (country == null) {
            LoadingView(modifier = Modifier.padding(innerPadding))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AsyncImage(
                    model = country.flagUrl,
                    contentDescription = "${country.commonName} flag",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                )
                CountryFacts(country)

                HorizontalDivider()
                NoteSection(
                    text = state.noteText,
                    hasSaved = state.hasSavedNote,
                    onChange = viewModel::onNoteChange,
                    onSave = viewModel::onSaveNote,
                    onDelete = viewModel::onDeleteNote
                )

                HorizontalDivider()
                CollectionsSection(
                    state = state,
                    onToggle = viewModel::onToggleCollection,
                    onCreate = viewModel::onCreateCollectionAndAdd
                )
            }
        }
    }
}

@Composable
private fun CountryFacts(country: Country) {
    Text(
        text = country.officialName.ifBlank { country.commonName },
        style = MaterialTheme.typography.headlineSmall
    )
    InfoRow("Region", listOf(country.region, country.subregion).filter { it.isNotBlank() }.joinToString(", "))
    InfoRow("Capital", country.capital.ifBlank { "—" })
    InfoRow("Population", NumberFormat.getIntegerInstance().format(country.population))
    if (country.languages.isNotEmpty()) InfoRow("Languages", country.languages.joinToString(", "))
    if (country.currencies.isNotEmpty()) InfoRow("Currencies", country.currencies.joinToString(", "))
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(label, modifier = Modifier.weight(0.4f), fontWeight = FontWeight.SemiBold)
        Text(value, modifier = Modifier.weight(0.6f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteSection(
    text: String,
    hasSaved: Boolean,
    onChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Text("My note", style = MaterialTheme.typography.titleMedium)
    OutlinedTextField(
        value = text,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Write a personal note") },
        minLines = 2
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onSave) { Text("Save note") }
        if (hasSaved) {
            OutlinedButton(onClick = onDelete) { Text("Delete") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollectionsSection(
    state: CountryDetailUiState,
    onToggle: (Long) -> Unit,
    onCreate: (String) -> Unit
) {
    Text("Collections", style = MaterialTheme.typography.titleMedium)
    if (state.collections.isEmpty()) {
        Text("No collections yet. Create one below.", style = MaterialTheme.typography.bodySmall)
    }
    state.collections.forEach { collection ->
        FilterChip(
            selected = state.memberCollectionIds.contains(collection.id),
            onClick = { onToggle(collection.id) },
            label = { Text(collection.name) }
        )
    }
    var newName by remember { mutableStateOf("") }
    OutlinedTextField(
        value = newName,
        onValueChange = { newName = it },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text("New collection name") }
    )
    TextButton(
        onClick = {
            if (newName.isNotBlank()) {
                onCreate(newName)
                newName = ""
            }
        }
    ) {
        Text("Create and add this country")
    }
}
