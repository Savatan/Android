package com.example.countries.ui.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.countries.ui.components.EmptyView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onCountryClick: (String) -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val recent by viewModel.recent.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recently viewed") },
                actions = {
                    if (recent.isNotEmpty()) {
                        IconButton(onClick = viewModel::onClear) {
                            Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear history")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (recent.isEmpty()) {
            EmptyView("Nothing viewed yet.", modifier = Modifier.padding(innerPadding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(recent, key = { it.code }) { country ->
                    Card(modifier = Modifier.fillMaxWidth().clickable { onCountryClick(country.code) }) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = country.flagUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(width = 56.dp, height = 40.dp).clip(RoundedCornerShape(6.dp))
                            )
                            Text(
                                country.commonName,
                                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }
    }
}
