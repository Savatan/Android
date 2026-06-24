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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.countries.domain.model.Country
import com.example.countries.ui.components.ErrorView
import com.example.countries.ui.components.LoadingView
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryDetailScreen(
    onBack: () -> Unit,
    viewModel: CountryDetailViewModel = hiltViewModel()
) {
    val state = viewModel.uiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val title = (state.content as? DetailContent.Success)?.country?.commonName ?: "Details"
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.content is DetailContent.Success) {
                        IconButton(onClick = viewModel::onToggleFavourite) {
                            Icon(
                                imageVector = if (state.isFavourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (state.isFavourite) "Remove from favourites" else "Add to favourites"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val content = state.content) {
            is DetailContent.Loading -> LoadingView(modifier = Modifier.padding(innerPadding))
            is DetailContent.Error -> ErrorView(
                message = content.message,
                onRetry = viewModel::onRetry,
                modifier = Modifier.padding(innerPadding)
            )
            is DetailContent.Success -> CountryDetailContent(
                country = content.country,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun CountryDetailContent(country: Country, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AsyncImage(
            model = country.flagUrl,
            contentDescription = country.flagAlt.ifBlank { "${country.commonName} flag" },
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
        )

        Text(
            text = country.officialName.ifBlank { country.commonName },
            style = MaterialTheme.typography.headlineSmall
        )

        HorizontalDivider()

        InfoRow("Region", listOf(country.region, country.subregion).filter { it.isNotBlank() }.joinToString(", "))
        InfoRow("Capital", country.capital.ifBlank { "—" })
        InfoRow("Population", formatNumber(country.population))
        InfoRow("Area", if (country.area > 0) "${formatNumber(country.area.toLong())} km²" else "—")
        InfoRow("Languages", country.languages.joinToString(", ").ifBlank { "—" })
        InfoRow("Currencies", country.currencies.joinToString(", ").ifBlank { "—" })
        InfoRow("Timezones", country.timezones.joinToString(", ").ifBlank { "—" })
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.weight(0.4f),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun formatNumber(value: Long): String =
    NumberFormat.getIntegerInstance().format(value)
