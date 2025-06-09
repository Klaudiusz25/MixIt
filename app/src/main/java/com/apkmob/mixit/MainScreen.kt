package com.apkmob.mixit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apkmob.mixit.data.Cocktail
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    val tabs = listOf("Strona główna", "Łatwe", "Trudne", "Alkoholowe", "Bezalkoholowe")
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(initialPage = 0)
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredCocktails = remember(cocktails, searchQuery) {
        if (searchQuery.isBlank()) {
            cocktails
        } else {
            cocktails.filter { cocktail ->
                cocktail.name.contains(searchQuery, ignoreCase = true) ||
                        cocktail.ingredients.any { it.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Kategorie", modifier = Modifier.padding(16.dp))
                Divider()
                tabs.forEachIndexed { index, title ->
                    if (index > 0) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                        drawerState.close()
                                    }
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (isSearchActive) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { isSearchActive = false },
                        onActiveChange = { isSearchActive = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    CenterAlignedTopAppBar(
                        title = { Text("Mix It!") },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(Icons.Default.Search, contentDescription = "Szukaj")
                            }
                        }
                    )
                }
            }
        ) { padding ->
            Column(modifier = Modifier.padding(padding)) {
                if (!isSearchActive) {
                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                                text = { Text(title) }
                            )
                        }
                    }
                }

                if (isSearchActive) {
                    if (filteredCocktails.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Nie znaleziono koktajli")
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            contentPadding = PaddingValues(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(filteredCocktails) { cocktail ->
                                CocktailCard(cocktail = cocktail, onClick = onSelect)
                            }
                        }
                    }
                } else {
                    HorizontalPager(
                        state = pagerState,
                        count = tabs.size,
                        modifier = Modifier.weight(1f)
                    ) { page ->
                        when (page) {
                            0 -> HomeScreen(cocktails = filteredCocktails, onSelect = onSelect)
                            1 -> { // Łatwe
                                val categoryCocktails = filteredCocktails.filter {
                                    it.category.equals("Easy", ignoreCase = true)
                                }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            2 -> { // Trudne
                                val categoryCocktails = filteredCocktails.filter {
                                    it.category.equals("Hard", ignoreCase = true)
                                }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            3 -> { // Alkoholowe
                                val categoryCocktails = filteredCocktails.filter { it.alcoholic }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            4 -> { // Bezalkoholowe
                                val categoryCocktails = filteredCocktails.filter { !it.alcoholic }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = onSearch,
        active = true,
        onActiveChange = onActiveChange,
        placeholder = { Text("Szukaj koktajli...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Szukaj") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = "Wyczyść")
                }
            }
        },
        modifier = modifier
    ) {
        // Możesz dodać sugestie wyszukiwania tutaj jeśli potrzebne
    }
}

@Composable
fun HomeScreen(cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Witaj w Mix It!",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Odkrywaj i twórz niesamowite koktajle z naszą aplikacją.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (cocktails.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Brak koktajli do wyświetlenia")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(cocktails) { cocktail ->
                    CocktailCard(cocktail = cocktail, onClick = onSelect)
                }
            }
        }
    }
}

@Composable
fun CategoryScreen(category: String, cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    if (cocktails.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Brak dostępnych koktajli $category")
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(cocktails) { cocktail ->
                CocktailCard(cocktail = cocktail, onClick = onSelect)
            }
        }
    }
}

@Composable
fun CocktailCard(cocktail: Cocktail, onClick: (Cocktail) -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable { onClick(cocktail) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp
        )
    ) {
        Column {
            AsyncImage(
                model = cocktail.imageUrl, // URL zdjęcia
                contentDescription = cocktail.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Text(
                text = cocktail.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}