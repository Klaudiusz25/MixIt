package com.apkmob.mixit

import androidx.compose.foundation.background
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
            // Obraz koktajlu - teraz będzie się zwijał razem z paskiem aplikacji
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.Default.LocalBar,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp).align(Alignment.Center)
                )
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

                // Sekcja timera i notatek
                if (cocktail.timer > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Czas potrzebny do przyrządzenia: ${viewModel.lastRecordedTime}s")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Aktualny czas: ${viewModel.timerValue}s")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.startTimer() }) {
                            Text("Start")
                        }
                        Button(onClick = { viewModel.pauseTimer() }) {
                            Text("Pauza")
                        }
                        Button(onClick = { viewModel.resumeTimer() }) {
                            Text("Wznów")
                        }
                        Button(onClick = { viewModel.stopTimer() }) {
                            Text("Stop")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = viewModel.note,
                        onValueChange = { viewModel.note = it },
                        label = { Text("Twoje notatki") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}