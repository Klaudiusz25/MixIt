package com.apkmob.mixit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.apkmob.mixit.data.Cocktail

@Composable
fun TabletDetailLayout(
    cocktails: List<Cocktail>,
    selectedId: Int,
    onBackPressed: () -> Unit
) {
    var selectedCocktail by remember { mutableStateOf(cocktails.find { it.id == selectedId }) }
    val viewModel: DetailViewModel = viewModel()

    Row(modifier = Modifier.fillMaxSize()) {
        // Lista koktajli (1/3 ekranu)
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(8.dp)
        ) {
            items(cocktails) { cocktail ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { selectedCocktail = cocktail },
                    colors = CardDefaults.cardColors(
                        containerColor = if (cocktail.id == selectedCocktail?.id)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = cocktail.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (!cocktail.imageUrl.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            AsyncImage(
                                model = cocktail.imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                            )
                        }
                    }
                }
            }
        }

        // Szczegóły koktajlu (2/3 ekranu)
        selectedCocktail?.let { cocktail ->
            Box(modifier = Modifier.weight(2f)) {
                viewModel.loadCocktail(cocktail.id)
                DetailScreen(
                    cocktail = cocktail,
                    viewModel = viewModel,
                    onBackPressed = onBackPressed,
                    isTablet = true
                )
            }
        } ?: run {
            Box(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Wybierz koktajl z listy")
            }
        }
    }
}