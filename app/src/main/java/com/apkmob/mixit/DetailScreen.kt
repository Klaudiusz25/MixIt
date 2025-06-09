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
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(cocktail: Cocktail, viewModel: DetailViewModel, onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
                text = { Text("Wyślij składniki") }
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
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Obraz koktajlu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp) // Większa wysokość dla pionowego układu
            ) {
                if (cocktail.imageUrl.isNotEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(cocktail.imageUrl),
                        contentDescription = "Zdjęcie koktajlu",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp), // Stała wysokość
                        contentScale = ContentScale.Fit // Pokazuje cały obrazek bez przycinania
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.LocalBar,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp).align(Alignment.Center)
                    )
                }
            }

            // Reszta zawartości
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Składniki:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = cocktail.ingredients.joinToString("\n"))

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Instrukcje:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = cocktail.instructions)

                // Sekcja timera
                if (cocktail.timer > 0) {
                    val formattedTime by remember(viewModel.timerValue) {
                        derivedStateOf {
                            val minutes = viewModel.timerValue / 60
                            val seconds = viewModel.timerValue % 60
                            String.format("%02d:%02d", minutes, seconds)
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                    ) {
                        // Okrągły timer
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(150.dp).align(Alignment.CenterHorizontally)
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
                                    text = "Timer",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = formattedTime,
                                    style = MaterialTheme.typography.displayMedium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Kontrolki timera - zachowane wszystkie 4 przyciski
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.startTimer() },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isTimerRunning && viewModel.timerValue == 0
                            ) {
                                Text("Start")
                            }

                            Button(
                                onClick = { viewModel.pauseTimer() },
                                modifier = Modifier.weight(1f),
                                enabled = viewModel.isTimerRunning
                            ) {
                                Text("Pauza")
                            }

                            Button(
                                onClick = { viewModel.resumeTimer() },
                                modifier = Modifier.weight(1f),
                                enabled = !viewModel.isTimerRunning && viewModel.timerValue > 0
                            ) {
                                Text("Wznów")
                            }

                            Button(
                                onClick = { viewModel.stopTimer() },
                                modifier = Modifier.weight(1f),
                                enabled = viewModel.timerValue > 0
                            ) {
                                Text("Stop")
                            }
                        }
                    }


                    // Sekcja notatek (działająca)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.cocktail?.notes ?: "",
                        onValueChange = { viewModel.updateNotes(it) },
                        label = { Text("Twoje notatki") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}