package com.apkmob.mixit

import androidx.compose.runtime.Composable
import com.apkmob.mixit.data.Cocktail

@Composable
fun MixItApp(cocktails: List<Cocktail>, onSelect: (Cocktail) -> Unit) {
    MainScreen(cocktails = cocktails, onSelect = onSelect)
}