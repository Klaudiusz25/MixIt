package com.apkmob.mixit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.apkmob.mixit.data.Cocktail
import com.apkmob.mixit.ui.theme.MixItTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.content.Intent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cocktails = loadCocktailsFromAssets()

        setContent {
            MixItTheme {
                MixItApp(
                    cocktails = cocktails,
                    onSelect = { cocktail ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("cocktail", cocktail)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun loadCocktailsFromAssets(): List<Cocktail> {
        val json = assets.open("cocktails.json").bufferedReader().use { it.readText() }
        return Gson().fromJson<List<Cocktail>>(
            json,
            object : TypeToken<List<Cocktail>>() {}.type
        )
    }
}