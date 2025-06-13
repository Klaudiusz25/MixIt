package com.apkmob.mixit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apkmob.mixit.data.Cocktail
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(cocktail: Cocktail, viewModel: DetailViewModel, onBackPressed: () -> Unit, isTablet: Boolean) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scrollState = rememberScrollState()

    fun prepareSmsMessage(): String {
        return buildString {
            append("Składniki koktajlu ${cocktail.name}:\n")
            append(cocktail.ingredients.joinToString("\n• ", "• "))
            append("\n\nInstrukcje:\n${cocktail.instructions}")
        }
    }



    fun openSmsApp() {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("smsto:")
            putExtra("sms_body", prepareSmsMessage())
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Nie znaleziono aplikacji do wysyłania SMS", Toast.LENGTH_SHORT)
                .show()
        }
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { openSmsApp() },
                modifier = Modifier.padding(bottom = 80.dp),
                icon = { Icon(Icons.Default.Send, contentDescription = "Wyślij") },
                text = { Text("") }
            )
        },
        topBar = {
            LargeTopAppBar(
                title = { Text(cocktail.name) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .padding(padding)
            ) {
                // Obraz koktajlu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    if (!cocktail.imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = rememberAsyncImagePainter(cocktail.imageUrl),
                            contentDescription = "Zdjęcie koktajlu",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.LocalBar,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp).align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Główna zawartość
                Column(modifier = Modifier.padding(16.dp)) {
                    // Sekcja składników
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Składniki:",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cocktail.ingredients.joinToString("\n") { "• $it" },
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sekcja instrukcji
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Instrukcje:",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = cocktail.instructions,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    // Sekcja timera
                    if (cocktail.timer > 0) {
                        val formattedTime by remember(viewModel.timerValue) {
                            derivedStateOf {
                                val minutes = viewModel.timerValue / 60
                                val seconds = viewModel.timerValue % 60
                                String.format("%02d:%02d", minutes, seconds)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "Timer",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Główny wiersz z timerem i przyciskami
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Przyciski kontrolne
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Button(
                                    onClick = { viewModel.startTimer() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !viewModel.isTimerRunning && viewModel.timerValue == viewModel.initialTimerValue
                                ) {
                                    Text("Start")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.pauseTimer() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = viewModel.isTimerRunning
                                ) {
                                    Text("Pauza")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.resumeTimer() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !viewModel.isTimerRunning && viewModel.timerValue > 0 && viewModel.timerValue < viewModel.initialTimerValue
                                ) {
                                    Text("Wznów")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { viewModel.stopTimer() },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = viewModel.timerValue > 0
                                ) {
                                    Text("Reset")
                                }
                            }

                            // Wizualizacja timera
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(150.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(150.dp),
                                    progress = {
                                        if (cocktail.timer > 0) {
                                            1f - (viewModel.timerValue.toFloat() / cocktail.timer.toFloat())
                                        } else {
                                            0f
                                        }
                                    },
                                    strokeWidth = 8.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = formattedTime,
                                        style = MaterialTheme.typography.displayMedium
                                    )
                                }
                            }
                        }

                        // Ustawienia timera
                        Spacer(modifier = Modifier.height(16.dp))
                        var newTimerValue by remember { mutableStateOf(cocktail.timer.toString()) }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newTimerValue,
                                onValueChange = { newValue ->
                                    if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\$"))) {
                                        newTimerValue = newValue
                                    }
                                },
                                label = { Text("Czas (sekundy)") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    val time = newTimerValue.toIntOrNull() ?: cocktail.timer
                                    viewModel.setTimer(time)
                                },
                                modifier = Modifier.height(56.dp)
                            ) {
                                Text("Ustaw")
                            }
                        }
                    }

                    // Sekcja notatek
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Twoje notatki",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = viewModel.cocktail?.notes ?: "",
                        onValueChange = { viewModel.updateNotes(it) },
                        label = { Text("Dodaj swoje uwagi...") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }
            }
        }
    }
}