package com.apkmob.mixit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.apkmob.mixit.data.Cocktail
import com.apkmob.mixit.ui.theme.MixItTheme

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val cocktail = intent.getParcelableExtra<Cocktail>("cocktail")
        if (cocktail == null) {
            Toast.makeText(this, "Błąd: brak koktajlu", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            MixItTheme {
                val viewModel: DetailViewModel by viewModels()
                DetailScreen(
                    cocktail = cocktail,
                    viewModel = viewModel,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}