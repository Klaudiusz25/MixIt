package com.apkmob.mixit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.LocalCafe
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
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import coil.compose.AsyncImage
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    val isTablet = isTablet()
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
                        NavigationDrawerItem(
                            label = { Text(title) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                    drawerState.close()
                                }
                            },
                            modifier = Modifier.padding(8.dp)
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
                if (!isSearchActive && !isTablet) {
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

                if (isSearchActive || isTablet) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(if (isTablet) 3 else 2),
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredCocktails) { cocktail ->
                            CocktailCard(cocktail = cocktail, onClick = onSelect)
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
                            1 -> {
                                val categoryCocktails = filteredCocktails.filter {
                                    it.category.equals("Easy", ignoreCase = true)
                                }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            2 -> {
                                val categoryCocktails = filteredCocktails.filter {
                                    it.category.equals("Hard", ignoreCase = true)
                                }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            3 -> {
                                val categoryCocktails = filteredCocktails.filter { it.alcoholic }
                                CategoryScreen(
                                    category = tabs[page],
                                    cocktails = categoryCocktails,
                                    onSelect = onSelect
                                )
                            }
                            4 -> {
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
        // Nagłówek z przywitaniem
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Witaj w Mix It!",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Odkrywaj i twórz niesamowite koktajle",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        // Sekcja polecanych koktajli
        Text(
            text = "Polecane koktajle",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        if (cocktails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Brak koktajli do wyświetlenia")
            }
        } else {
            // Ulepszone karty koktajli
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cocktails) { cocktail ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .clickable { onSelect(cocktail) },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Column {
                            // Obraz koktajlu z overlayem nazwy
                            Box(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = cocktail.imageUrl,
                                    contentDescription = cocktail.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                ),
                                                startY = 0.6f
                                            )
                                        )
                                )

                                // Nazwa koktajlu na dole obrazka
                                Text(
                                    text = cocktail.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                )
                            }

                            // Informacje dodatkowe
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ikona alkoholu
                                Icon(
                                    imageVector = if (cocktail.alcoholic) Icons.Default.LocalBar else Icons.Default.LocalCafe,
                                    contentDescription = if (cocktail.alcoholic) "Alkoholowy" else "Bezalkoholowy",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                // Trudność
                                Text(
                                    text = when {
                                        cocktail.category.equals("easy", ignoreCase = true) -> "Łatwy"
                                        cocktail.category.equals("hard", ignoreCase = true) -> "Trudny"
                                        else -> cocktail.category
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryScreen(category: String, cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    Column(modifier = Modifier.padding(16.dp)) {
        // Nagłówek z nazwą kategorii
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Znaleziono ${cocktails.size} koktajli",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (cocktails.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Brak koktajli w tej kategorii")
            }
        } else {
            // Ulepszone karty koktajli - takie same jak na stronie głównej
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cocktails) { cocktail ->
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.75f)
                            .clickable { onSelect(cocktail) },
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Column {
                            // Obraz koktajlu z overlayem nazwy
                            Box(modifier = Modifier.weight(1f)) {
                                AsyncImage(
                                    model = cocktail.imageUrl,
                                    contentDescription = cocktail.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Gradient overlay
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            brush = Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color.Black.copy(alpha = 0.7f)
                                                ),
                                                startY = 0.6f
                                            )
                                        )
                                )

                                // Nazwa koktajlu na dole obrazka
                                Text(
                                    text = cocktail.name,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .padding(12.dp)
                                )
                            }

                            // Informacje dodatkowe
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ikona alkoholu
                                Icon(
                                    imageVector = if (cocktail.alcoholic) Icons.Default.LocalBar else Icons.Default.LocalCafe,
                                    contentDescription = if (cocktail.alcoholic) "Alkoholowy" else "Bezalkoholowy",
                                    tint = MaterialTheme.colorScheme.primary
                                )

                                // Trudność
                                Text(
                                    text = when {
                                        cocktail.category.equals("easy", ignoreCase = true) -> "Łatwy"
                                        cocktail.category.equals("hard", ignoreCase = true) -> "Trudny"
                                        else -> cocktail.category
                                    },
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
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