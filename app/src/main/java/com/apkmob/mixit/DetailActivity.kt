package com.apkmob.mixit

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.apkmob.mixit.data.Cocktail
import com.apkmob.mixit.ui.theme.MixItTheme

class DetailActivity : ComponentActivity() {
    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(
            application,
            intent.getParcelableExtra<Cocktail>("cocktail")?.id ?: 0
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isTablet = resources.getBoolean(R.bool.isTablet)

        if (isTablet) {
            setContent {
                MixItTheme {
                    val cocktails = CocktailStorage.loadCocktails(this)
                    val selectedId = intent.getParcelableExtra<Cocktail>("cocktail")?.id ?: 0
                    TabletDetailLayout(
                        cocktails = cocktails,
                        selectedId = selectedId,
                        onBackPressed = { finish() }
                    )
                }
            }
        } else {
            val cocktail = intent.getParcelableExtra<Cocktail>("cocktail")
            if (cocktail == null) {
                Toast.makeText(this, "Błąd: brak koktajlu", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            setContent {
                MixItTheme {
                    viewModel.loadCocktail(cocktail.id)
                    DetailScreen(
                        cocktail = cocktail,
                        viewModel = viewModel,
                        onBackPressed = { finish() },
                        isTablet = false
                    )
                }
            }
        }
    }
}